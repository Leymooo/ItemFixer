package ru.leymooo.fixer;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.apache.commons.codec.binary.Base64;
import org.bukkit.FireworkEffect;
import org.bukkit.Material;
import org.bukkit.block.ShulkerBox;
import org.bukkit.block.banner.Pattern;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.BannerMeta;
import org.bukkit.inventory.meta.BlockStateMeta;
import org.bukkit.inventory.meta.FireworkMeta;
import org.bukkit.inventory.meta.ItemMeta;

import ru.leymooo.fixer.utils.MiniNbtFactory;

import com.comphenix.protocol.wrappers.nbt.NbtBase;
import com.comphenix.protocol.wrappers.nbt.NbtCompound;
import com.comphenix.protocol.wrappers.nbt.NbtList;

public class ItemChecker {

    private Boolean removeInvalidEnch;
    private Boolean checkench;
    private HashSet<String> nbt = new HashSet<String>();
    private HashSet<String> world = new HashSet<String>();
    private HashSet<Material> tiles = new HashSet<Material>();
    private HashSet<String> ignoreNbt = new HashSet<String>();
    private Main plugin;

    public ItemChecker(Main main) {
        this.plugin = main;
        ignoreNbt.addAll(plugin.getConfig().getStringList("ignored-tags"));
        nbt.addAll(Arrays.asList("ActiveEffects", "Command", "CustomName", "AttributeModifiers", "Unbreakable", "CustomPotionEffects"));
        nbt.removeAll(ignoreNbt);
        tiles.addAll(Arrays.asList(
                Material.FURNACE, Material.CHEST, Material.TRAPPED_CHEST, Material.DROPPER, Material.DISPENSER, Material.COMMAND_MINECART, Material.HOPPER_MINECART,
                Material.HOPPER, Material.BREWING_STAND_ITEM, Material.BEACON, Material.SIGN, Material.MOB_SPAWNER, Material.NOTE_BLOCK, Material.COMMAND, Material.JUKEBOX));
        for (String w : plugin.getConfig().getStringList("ignore-worlds")) {
            world.add(w.toLowerCase());
        }
        checkench = plugin.getConfig().getBoolean("check-enchants");
        removeInvalidEnch = plugin.getConfig().getBoolean("remove-invalid-enchants");
    }

    @SuppressWarnings("rawtypes")
    public boolean isCrashSkull(NbtCompound tag) {
        if (tag.containsKey("SkullOwner")) {
            NbtCompound skullOwner = tag.getCompound("SkullOwner");
            if (skullOwner.containsKey("Properties")) {
                NbtCompound properties = skullOwner.getCompound("Properties");
                if (properties.containsKey("textures")) {
                    NbtList<NbtBase> textures = properties.getList("textures");
                    for (NbtBase texture : textures.asCollection()) {
                        if (texture instanceof NbtCompound) {
                            if (((NbtCompound) texture).containsKey("Value")) {
                                if (((NbtCompound) texture).getString("Value").trim().length() > 0) {
                                    String decoded = null;
                                    try {
                                        decoded = new String(Base64.decodeBase64(((NbtCompound) texture).getString("Value")));
                                    } catch (Exception e) {
                                        tag.remove("SkullOwner");
                                        return true;
                                    }
                                    if (decoded == null || decoded.isEmpty()) {
                                        tag.remove("SkullOwner");
                                        return true;
                                    }
                                    if (decoded.contains("textures") && decoded.contains("SKIN")) {
                                        if (decoded.contains("url")) {
                                            String headUrl = null;
                                            try {
                                                headUrl = decoded.split("url\":")[1].replace("}", "").replace("\"", "");
                                            } catch (ArrayIndexOutOfBoundsException e) {
                                                tag.remove("SkullOwner");
                                                return true;
                                            }
                                            if (headUrl == null || headUrl.isEmpty() || headUrl.trim().length() == 0) {
                                                tag.remove("SkullOwner");
                                                return true;
                                            }
                                            if (headUrl.startsWith("http://textures.minecraft.net/texture/") || headUrl.startsWith("https://textures.minecraft.net/texture/")) {
                                                return false;
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
                tag.remove("SkullOwner");
                return true;
            }
        }
        return false;
    }

    private boolean checkEnchants(ItemStack stack, Player p) {
        boolean cheat = false;
        if (checkench && !p.hasPermission("itemfixer.bypass.enchant") && stack.hasItemMeta() && stack.getItemMeta().hasEnchants()) {
            final ItemMeta meta = stack.getItemMeta();
            for (Map.Entry<Enchantment, Integer> ench : meta.getEnchants().entrySet()) {
                Enchantment Enchant = ench.getKey();
                String perm = "itemfixer.allow."+stack.getType().toString()+"."+Enchant.getName()+"."+ench.getValue();
                if (removeInvalidEnch && !Enchant.canEnchantItem(stack) && !p.hasPermission(perm) ) {
                    meta.removeEnchant(Enchant);
                    cheat = true;
                }
                if ((ench.getValue() > Enchant.getMaxLevel() || ench.getValue() < 0) && !p.hasPermission(perm)) {
                    meta.removeEnchant(Enchant);
                    cheat = true;
                }
            }
            if (cheat) stack.setItemMeta(meta);
        }
        return cheat;
    }

    private boolean checkNbt(ItemStack stack, Player p) {
        boolean cheat = false;
        try {
            if (p.hasPermission("itemfixer.bypass.nbt")) return false;
            Material mat = stack.getType();
            NbtCompound tag = (NbtCompound) MiniNbtFactory.fromItemTag(stack);
            if (tag == null) return false;
            if(this.isCrashItem(stack, tag, mat)) {
                tag.getKeys().clear();
                stack.setAmount(1);
                return true;
            }
            final String tagS = tag.toString();
            for (String nbt1 : nbt) {
                if (tag.containsKey(nbt1)) {
                    tag.remove(nbt1);
                    cheat = true;
                }
            }
            if (tag.containsKey("BlockEntityTag") && !isShulkerBox(stack) && mat != Material.BANNER && !ignoreNbt.contains("BlockEntityTag") ) {
                tag.remove("BlockEntityTag");
                cheat = true;
            } else if (mat == Material.WRITTEN_BOOK && ((!ignoreNbt.contains("ClickEvent") && tagS.contains("ClickEvent"))
                    || (!ignoreNbt.contains("run_command") && tagS.contains("run_command")))) {
                tag.getKeys().clear();
                cheat = true;
            } else if (mat == Material.MONSTER_EGG && !ignoreNbt.contains("EntityTag") && tag.containsKey("EntityTag") && fixEgg(tag)) {
                cheat = true;
            } else if (mat == Material.ARMOR_STAND && !ignoreNbt.contains("EntityTag") && tag.containsKey("EntityTag")) {
                tag.remove("EntityTag");
                cheat = true;
            } else if ((mat == Material.SKULL || mat == Material.SKULL_ITEM) && stack.getDurability() == 3) {
                if (isCrashSkull(tag)) {
                    cheat = true;
                }
            } else if (mat == Material.FIREWORK && !ignoreNbt.contains("Explosions") && checkFireWork(stack)) {
                cheat = true;
            } else if (mat == Material.BANNER && checkBanner(stack)) {
                cheat = true;
            }
        } catch (Exception e) {
        }
        return cheat;
    }

    private void checkShulkerBox(ItemStack stack, Player p) {
        if (!isShulkerBox(stack)) return;
        BlockStateMeta meta = (BlockStateMeta) stack.getItemMeta();
        ShulkerBox box = (ShulkerBox) meta.getBlockState();
        for (ItemStack is : box.getInventory().getContents()) {
            if (isShulkerBox(is) || isHackedItem(is, p)) {
                box.getInventory().clear();
                meta.setBlockState(box);
                stack.setItemMeta(meta);
                return;
            }
        }
    }

    private boolean isShulkerBox(ItemStack stack) {
        if (stack == null || stack.getType() == Material.AIR) return false;
        if (!plugin.isUnsupportedVersion()) return false;
        if (!stack.hasItemMeta()) return false;
        if (!(stack.getItemMeta() instanceof BlockStateMeta)) return false;
        BlockStateMeta meta = (BlockStateMeta) stack.getItemMeta();
        return meta.getBlockState() instanceof ShulkerBox;
    }

    public boolean isHackedItem(ItemStack stack, Player p) {
        if (stack == null || stack.getType() == Material.AIR) return false;
        if (this.world.contains(p.getWorld().getName().toLowerCase()) || plugin.isMagicItem(stack)) return false;
        this.checkShulkerBox(stack, p);
        if (this.checkNbt(stack, p)) {
            checkEnchants(stack, p);
            return true;
        }
        return checkEnchants(stack, p);
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

    public boolean checkFireWork(ItemStack stack) {
        boolean changed = false;
        FireworkMeta meta = (FireworkMeta) stack.getItemMeta();
        if (meta.getPower() > 3) {
            meta.setPower(3);
            changed = true;
        }
        if (meta.getEffectsSize() > 8) {
            List<FireworkEffect> list = meta.getEffects().stream().limit(8).collect(Collectors.toList());
            meta.clearEffects();
            meta.addEffects(list);
            changed = true;
        }
        if (changed) {
            stack.setItemMeta(meta);
        }
        return changed;
    }

    private boolean isCrashItem(ItemStack stack, NbtCompound tag, Material mat) {
        if (stack.getAmount() <1 || stack.getAmount() > 64 || tag.getKeys().size() > 20) {
            return true;
        }
        int tagL = tag.toString().length();
        if ((mat == Material.NAME_TAG || tiles.contains(mat)) && tagL > 600) {
            return true;
        }
        if (isShulkerBox(stack)) return false;
        return mat == Material.WRITTEN_BOOK ? (tagL >= 22000) : (tagL >= 13000);
    }

    private boolean fixEgg(NbtCompound tag) {
        NbtCompound enttag = tag.getCompound("EntityTag");
        int size = enttag.getKeys().size();
        if (size >= 2 ) {
            Object id = enttag.getObject("id");
            Object color = enttag.getObject("Color");
            enttag.getKeys().clear();
            if (id != null && id instanceof String) {
                enttag.put("id", (String) id);
            }
            if (color != null && color instanceof Byte) {
                enttag.put("Color", (byte) color);
            }
            tag.put("EntityTag", enttag);
            return color==null ? true : size >= 3;
        }
        return false;
    }
}
