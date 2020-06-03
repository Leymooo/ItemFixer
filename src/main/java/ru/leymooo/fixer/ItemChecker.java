package ru.leymooo.fixer;

import com.comphenix.protocol.wrappers.nbt.NbtBase;
import com.comphenix.protocol.wrappers.nbt.NbtCompound;
import com.comphenix.protocol.wrappers.nbt.NbtList;
import com.comphenix.protocol.wrappers.nbt.NbtType;
import com.google.gson.Gson;
import com.google.gson.JsonObject;
import org.bukkit.Color;
import org.bukkit.FireworkEffect;
import org.bukkit.Material;
import org.bukkit.block.ShulkerBox;
import org.bukkit.block.banner.Pattern;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.*;
import org.bukkit.potion.PotionEffect;
import ru.leymooo.fixer.utils.MiniNbtFactory;
import ru.leymooo.fixer.utils.VersionUtils;

import java.util.*;
import java.util.stream.Collectors;

public class ItemChecker {

    private final Gson gson = new Gson();
    private final Set<String> nbt;
    private final Set<String> world;
    private final Set<Material> tiles;
    private final Set<String> ignoreNbt;
    private final Main plugin;
    private boolean removeInvalidEnch;
    private boolean checkench;

    public ItemChecker(Main main) {
        this.plugin = main;
        ignoreNbt = new HashSet<>(plugin.getConfig().getStringList("ignored-tags"));
        nbt = new HashSet<>(Arrays.asList("ActiveEffects", "Command", "CustomName", "AttributeModifiers", "Unbreakable"));
        nbt.removeAll(ignoreNbt);
        nbt.addAll(Arrays.asList("ActiveEffects", "Command", "CustomName", "AttributeModifiers", "Unbreakable"));
        nbt.removeAll(ignoreNbt);
        tiles = EnumSet.copyOf(Arrays.asList(
                Material.FURNACE, Material.CHEST, Material.TRAPPED_CHEST, Material.DROPPER, Material.DISPENSER, Material.COMMAND_MINECART, Material.HOPPER_MINECART,
                Material.HOPPER, Material.BREWING_STAND_ITEM, Material.BEACON, Material.SIGN, Material.MOB_SPAWNER, Material.NOTE_BLOCK, Material.COMMAND, Material.JUKEBOX));

        world = new HashSet<>(plugin.getConfig().getStringList("ignore-worlds"));
        checkench = plugin.getConfig().getBoolean("check-enchants");
        removeInvalidEnch = plugin.getConfig().getBoolean("remove-invalid-enchants");
    }

    @SuppressWarnings("rawtypes")
    public boolean isCrashSkull(NbtCompound tag) {
        if (!tag.containsKey("SkullOwner")) return false;
        NbtCompound skullOwner = tag.getCompound("SkullOwner");
        if (!skullOwner.containsKey("Properties")) return false;
        NbtCompound properties = skullOwner.getCompound("Properties");
        if (!properties.containsKey("textures")) return true;
        NbtList<NbtBase> textures = properties.getList("textures");
        for (NbtBase texture : textures.asCollection()) {
            if (!(texture instanceof NbtCompound)) continue;
            if (!((NbtCompound) texture).containsKey("Value")) continue;
            if (((NbtCompound) texture).getString("Value").trim().length() > 0) {
                String decoded = null;
                try {
                    decoded = new String(Base64.getDecoder().decode(((NbtCompound) texture).getString("Value")));
                } catch (Exception e) {
                    decoded = new String(Base64.getMimeDecoder().decode(((NbtCompound) texture).getString("Value")));
                }
                if (decoded.isEmpty()) return true;
                if (decoded.contains("textures")) {
                    JsonObject jdecoded = gson.fromJson(decoded, JsonObject.class);
                    if (!jdecoded.has("textures")) return false;
                    JsonObject jtextures = jdecoded.getAsJsonObject("textures");
                    if (!jtextures.has("SKIN")) return false;
                    JsonObject jskin = jtextures.getAsJsonObject("SKIN");
                    if (!jskin.has("url")) return false;
                    String url = jskin.getAsJsonPrimitive("url").getAsString();

                    if (url.isEmpty() || url.trim().length() == 0) return true;
                    if (url.startsWith("http://textures.minecraft.net/texture/") || url.startsWith("https://textures.minecraft.net/texture/")) {
                        return false;
                    }
                }
            }

        }
        return true;
    }

    private boolean checkEnchants(ItemStack stack, Player p, NbtCompound tag) {
        if (!checkench) return false;
        if (p.hasPermission("itemfixer.bypass.enchant")) return false;
        if (!tag.containsKey("ench")) return false;
        NbtList<NbtBase> enchants = tag.getList("ench");
        if (enchants.size() <= 0) return false;
        boolean cheat = false;

        List<NbtBase> enchCopy = new ArrayList<>(enchants.asCollection());
        for (NbtBase nbtBase : enchCopy) {
            if (!(nbtBase instanceof NbtCompound)) {
                tag.remove("ench");
                return true;
            }
            NbtCompound ench = (NbtCompound) nbtBase;
            try {
                int lvl = ((Number) ench.getValue("lvl").getValue()).intValue();
                Enchantment enchantment = Enchantment.getById(((Number) ench.getValue("id").getValue()).intValue());
                String perm = "itemfixer.allow." + stack.getType().toString() + "." + enchantment.getName() + "." + lvl;
                if (p.hasPermission(perm)) continue;
                if ((removeInvalidEnch && !enchantment.canEnchantItem(stack)) || (lvl > enchantment.getMaxLevel() || lvl < 0)) {
                    enchants.remove(nbtBase);
                    cheat = true;
                }
            } catch (Exception e) {
                e.printStackTrace();
                enchants.remove(nbtBase);
                cheat = true;
            }
        }
        return cheat;
    }

    private CheckStatus checkNbt(ItemStack stack, Player p, NbtCompound tag) {
        CheckStatus cheat = CheckStatus.GOOD;
        try {
            Material mat = stack.getType();
            if (this.isCrashItem(stack, tag, mat)) {
                tag.getKeys().clear();
                stack.setAmount(1);
                return CheckStatus.FIXED;
            }
            final String tagS = tag.toString();
            for (String nbt1 : nbt) {
                if (tag.containsKey(nbt1)) {
                    tag.remove(nbt1);
                    cheat = CheckStatus.FIXED;
                }
            }
            if (tag.containsKey("BlockEntityTag") && !isShulkerBox(stack, stack) && !needIgnore(stack)
                    && !ignoreNbt.contains("BlockEntityTag")) {
                tag.remove("BlockEntityTag");
                cheat = CheckStatus.FIXED;
            } else if (mat == Material.WRITTEN_BOOK && ((!ignoreNbt.contains("ClickEvent") && tagS.contains("ClickEvent"))
                    || (!ignoreNbt.contains("run_command") && tagS.contains("run_command")))) {
                tag.getKeys().clear();
                cheat = CheckStatus.FIXED;
            } else if (mat == Material.MONSTER_EGG && !ignoreNbt.contains("EntityTag") && tag.containsKey("EntityTag") && fixEgg(tag)) {
                cheat = CheckStatus.FIXED;
            } else if (mat == Material.ARMOR_STAND && !ignoreNbt.contains("EntityTag") && tag.containsKey("EntityTag")) {
                tag.remove("EntityTag");
                cheat = CheckStatus.FIXED;
            } else if ((mat == Material.SKULL || mat == Material.SKULL_ITEM) && stack.getDurability() == 3) {
                if (isCrashSkull(tag)) {
                    cheat = CheckStatus.FIXED;
                }
            } else if (mat == Material.FIREWORK && checkFireWork(stack, tag)) {
                cheat = CheckStatus.FIXED;
            } else if (mat == Material.BANNER && checkBanner(stack)) {
                cheat = CheckStatus.FIXED;
            } else if (isPotion(stack) && !ignoreNbt.contains("CustomPotionEffects") && tag.containsKey("CustomPotionEffects")
                    && (checkPotion(stack, p) || checkCustomColor(tag.getCompound("CustomPotionEffects")))) {
                cheat = CheckStatus.FIXED;
            }
        } catch (Exception e) {
            cheat = CheckStatus.FAILED;
            e.printStackTrace();
        }
        return cheat;
    }

    private boolean needIgnore(ItemStack stack) {
        Material m = stack.getType();
        return (m == Material.BANNER || (VersionUtils.isVersion(9) && (m == Material.SHIELD)));
    }

    private void checkShulkerBox(ItemStack stack, Player p) {
        if (!isShulkerBox(stack, stack))
            return;
        BlockStateMeta meta = (BlockStateMeta) stack.getItemMeta();
        ShulkerBox box = (ShulkerBox) meta.getBlockState();
        for (ItemStack is : box.getInventory().getContents()) {
            if (isShulkerBox(is, stack) || isHackedItem(is, p) != CheckStatus.GOOD) {
                box.getInventory().clear();
                meta.setBlockState(box);
                stack.setItemMeta(meta);
                return;
            }
        }
    }

    private boolean isPotion(ItemStack stack) {
        if (stack.getType() == Material.POTION) return true;
        Material m = stack.getType();
        return (VersionUtils.isVersion(9) && (m == Material.SPLASH_POTION || m == Material.LINGERING_POTION || m == Material.TIPPED_ARROW));
    }

    private boolean checkCustomColor(NbtCompound tag) {
        if (tag.containsKey("CustomPotionColor")) {
            int color = tag.getInteger("CustomPotionColor");
            try {
                Color.fromBGR(color);
            } catch (IllegalArgumentException e) {
                tag.remove("CustomPotionColor");
                return true;
            }
        }
        return false;
    }

    private boolean checkPotion(ItemStack stack, Player p) {
        boolean cheat = false;
        if (!p.hasPermission("itemfixer.bypass.potion")) {
            PotionMeta meta = (PotionMeta) stack.getItemMeta();
            for (PotionEffect ef : meta.getCustomEffects()) {
                String perm =
                        "itemfixer.allow.".concat(ef.getType().toString()).concat(".").concat(String.valueOf(ef.getAmplifier() + 1));
                if (!p.hasPermission(perm)) {
                    meta.removeCustomEffect(ef.getType());
                    cheat = true;
                }
            }
            if (cheat) {
                stack.setItemMeta(meta);
            }
        }
        return cheat;
    }

    private boolean isShulkerBox(ItemStack stack, ItemStack rootStack) {
        if (stack == null || stack.getType() == Material.AIR)
            return false;
        if (!VersionUtils.isVersion(11)) {
            return false;
        }
        //todo: material set
        if (stack.getType().name().endsWith("SHULKER_BOX")) {
            try {
                ItemMeta itemMeta = stack.getItemMeta();
            } catch (IllegalArgumentException e) {
                clearData(rootStack); //Уууух. Костылики
                return false;
            }
            return true;
        }

        return false;
    }

    public CheckStatus isHackedItem(ItemStack stack, Player p) {
        if (stack == null || stack.getType() == Material.AIR)
            return CheckStatus.GOOD;
        if (this.world.contains(p.getWorld().getName().toLowerCase()) || plugin.isMagicItem(stack))
            return CheckStatus.GOOD;

        this.checkShulkerBox(stack, p);
        boolean ignoreNbt = p.hasPermission("itemfixer.bypass.nbt");
        boolean ignoreEnchants = p.hasPermission("itemfixer.bypass.enchant");
        if (ignoreNbt && ignoreEnchants) {
            return CheckStatus.GOOD;
        }
        NbtCompound tag = null;
        try {
            tag = (NbtCompound) MiniNbtFactory.fromItemTag(stack);
        } catch (Exception e) {
            return CheckStatus.FAILED;
        }
        if (tag == null) {
            return CheckStatus.GOOD;
        }
        CheckStatus checkStatus = CheckStatus.GOOD;
        if (!ignoreNbt) {
            checkStatus = checkNbt(stack, p, tag);
            if (checkStatus == CheckStatus.FAILED) {
                return checkStatus;
            }
        }
        if (!ignoreEnchants && checkEnchants(stack, p, tag)) {
            return CheckStatus.FIXED;
        }
        return checkStatus;
    }


    private boolean checkBanner(ItemStack stack) {
        ItemMeta meta = stack.getItemMeta();
        boolean cheat = false;
        if (meta instanceof BannerMeta) {
            BannerMeta bmeta = (BannerMeta) meta;
            ArrayList<Pattern> patterns = new ArrayList<Pattern>();
            for (Pattern pattern : bmeta.getPatterns()) {
                if (pattern.getPattern() == null) {
                    cheat = true;
                    continue;
                }
                patterns.add(pattern);
            }
            if (cheat) {
                bmeta.setPatterns(patterns);
                stack.setItemMeta(bmeta);
            }
        }
        return cheat;
    }

    public boolean checkFireWork(ItemStack stack, NbtCompound tag) {
        boolean changed = false;
        FireworkMeta meta = (FireworkMeta) stack.getItemMeta();
        if (meta.getPower() > 3 || meta.getPower() < 0) {
            meta.setPower(3);
            changed = true;
        }
        if (!ignoreNbt.contains("Explosions")) { //bug???
            if (meta.hasEffects() && meta.getEffectsSize() > 8) {
                List<FireworkEffect> list = meta.getEffects().stream().limit(8).filter(ef -> ef.getColors().size() <= 8)
                        .filter(ef -> ef.getFadeColors().size() <= 8).collect(Collectors.toList());
                meta.clearEffects();
                meta.addEffects(list);
                changed = true;
            } else if (meta.hasEffects()) {
                for (FireworkEffect ef : meta.getEffects()) {
                    if (ef.getColors().size() > 8 || ef.getFadeColors().size() >= 8) {
                        meta.clearEffects();
                        changed = true;
                        break;
                    }
                }
            }
        }
        if (changed) {
            stack.setItemMeta(meta);
        }
        return changed;
    }

    private boolean isCrashItem(ItemStack stack, NbtCompound tag, Material mat) {
        if (stack.getAmount() < 1 || stack.getAmount() > 64 || tag.getKeys().size() > 20) {
            return true;
        }
        int tagL = tag.toString().length();
        if (plugin.isLongArraysSupported() && hasLongArrayTag(tag)) {
            return true;
        }
        if ((mat == Material.NAME_TAG || tiles.contains(mat)) && tagL > 600) {
            return true;
        }
        if (isShulkerBox(stack, stack))
            return false;
        return mat == Material.WRITTEN_BOOK ? (tagL >= 16500) : (tagL >= 13000);
    }

    private boolean fixEgg(NbtCompound tag) {
        NbtCompound enttag = tag.getCompound("EntityTag");
        int size = enttag.getKeys().size();
        if (size >= 2) {
            Object id = enttag.getObject("id");
            Object color = enttag.getObject("Color");
            enttag.getKeys().clear();
            if (id instanceof String) {
                enttag.put("id", (String) id);
            }
            if (color instanceof Byte) {
                enttag.put("Color", (byte) color);
            }
            tag.put("EntityTag", enttag);
            return color == null ? true : size >= 3;
        }
        return false;
    }

    private void clearData(ItemStack stack) {
        NbtCompound tag = (NbtCompound) MiniNbtFactory.fromItemTag(stack);
        if (tag == null)
            return;
        tag.getKeys().clear();
    }

    private boolean hasLongArrayTag(NbtCompound tag) {
        for (NbtBase nbt : tag) {
            if (nbt.getType() == NbtType.TAG_LONG_ARRAY) {
                return true;
            }
            if (nbt.getType() == NbtType.TAG_COMPOUND && hasLongArrayTag((NbtCompound) nbt)) {
                return true;
            }
            if (nbt.getType() == NbtType.TAG_LIST && hasLongArrayTagInList((NbtList) nbt)) {
                return true;
            }
        }
        return false;
    }

    private boolean hasLongArrayTagInList(NbtList list) {
        if (list.size() == 0) return false;
        for (Object o : list.asCollection()) {
            if (o instanceof NbtCompound && hasLongArrayTag((NbtCompound) o)) {
                return true;
            }
            if (o instanceof NbtList && hasLongArrayTagInList((NbtList) o)) {
                return true;
            }
        }
        return false;
    }

    public enum CheckStatus {
        FIXED,
        FAILED,
        GOOD
    }
}
