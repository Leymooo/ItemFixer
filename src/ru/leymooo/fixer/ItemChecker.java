package ru.leymooo.fixer;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Map;

import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import ru.leymooo.fixer.utils.MiniNbtFactory;

import com.comphenix.protocol.wrappers.nbt.NbtBase;
import com.comphenix.protocol.wrappers.nbt.NbtCompound;
import com.comphenix.protocol.wrappers.nbt.NbtList;
import com.google.common.io.BaseEncoding;

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
                Material.HOPPER, Material.BREWING_STAND_ITEM, Material.BEACON, Material.SIGN, Material.MOB_SPAWNER, Material.NOTE_BLOCK, Material.COMMAND));
        for (String w : plugin.getConfig().getStringList("ignore-worlds")) {
            world.add(w.toLowerCase());
        }
        checkench = plugin.getConfig().getBoolean("check-enchants");
        removeInvalidEnch = plugin.getConfig().getBoolean("remove-invalid-enchants");
    }
    //Онет. Пришлось вернуть этот ужас((
    @SuppressWarnings("rawtypes")
    public boolean isExploitSkull(NbtCompound root) {
        // Item
        if (root.containsKey("SkullOwner")) {
            NbtCompound skullOwner = root.getCompound("SkullOwner");
            if (skullOwner.containsKey("Properties")) {
                NbtCompound properties = skullOwner.getCompound("Properties");
                if (properties.containsKey("textures")) {
                    NbtList<NbtBase> textures = properties.getList("textures");
                    for (NbtBase texture : textures.asCollection()) {
                        if (texture instanceof NbtCompound) {
                            // Check for value
                            if (((NbtCompound) texture).containsKey("Value")) {
                                if (((NbtCompound) texture).getString("Value").trim().length() > 0) {
                                    String decoded = null;
                                    try {
                                        decoded = new String(BaseEncoding.base64().decode(((NbtCompound) texture).getString("Value")));
                                    } catch (Exception e) {
                                        root.remove("SkullOwner");
                                        return true;
                                    }
                                    if (decoded == null || decoded.isEmpty()) {
                                        root.remove("SkullOwner");
                                        return true;
                                    }
                                    if (decoded.contains("textures") && decoded.contains("SKIN")) {
                                        if (decoded.contains("url")) {
                                            String Url = decoded.split("url\":")[1].replace("}", "").replace("\"", "");
                                            if (Url.isEmpty() || Url.trim().length() == 0) {
                                                root.remove("SkullOwner");
                                                return true;
                                            }
                                            if (!Url.startsWith("http://textures.minecraft.net/texture/")) {
                                                root.remove("SkullOwner");
                                                return true;
                                            }
                                        } else {
                                            root.remove("SkullOwner");
                                            return true;
                                        }
                                    } else {
                                        root.remove("SkullOwner");
                                        return true;
                                    }
                                } else {
                                    root.remove("SkullOwner");
                                    return true;
                                }
                            } else {
                                root.remove("SkullOwner");
                                return true;
                            }
                        }
                    }
                } else {
                    root.remove("SkullOwner");
                    return true;
                }
            } else {
                root.remove("SkullOwner");
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
                if (removeInvalidEnch && !Enchant.canEnchantItem(stack) ) {
                    meta.removeEnchant(Enchant);
                    cheat = true;
                }
                if (ench.getValue() > Enchant.getMaxLevel() || ench.getValue() < 0) {
                    meta.removeEnchant(Enchant);
                    cheat = true;
                }
            }
            if (cheat) stack.setItemMeta(meta);
        }
        return cheat;
    }
    
    private boolean checkNbt(ItemStack stack, Player p) {
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
            nbt.stream().filter(tag.getKeys()::contains).forEach(tag::remove);
            if (tiles.contains(mat) && !ignoreNbt.contains("BlockEntityTag") &&  tag.containsKey("BlockEntityTag")) {
                tag.remove("BlockEntityTag");
            } else if (mat == Material.WRITTEN_BOOK && ((!ignoreNbt.contains("ClickEvent") && tagS.contains("ClickEvent"))
                    || (!ignoreNbt.contains("run_command") && tagS.contains("run_command")))) {
                tag.getKeys().clear();
            } else if (mat == Material.MONSTER_EGG && !ignoreNbt.contains("EntityTag") && (tag.containsKey("EntityTag") && tag.getCompound("EntityTag").getKeys().size()>=2)) {
                tag.put("EntityTag",getClearEntityTag(tag.getCompound("EntityTag")));
            } else if (mat == Material.ARMOR_STAND && !ignoreNbt.contains("EntityTag") && tag.containsKey("EntityTag")) {
                tag.remove("EntityTag");
            } else if ((mat == Material.SKULL || mat == Material.SKULL_ITEM) && stack.getDurability() == 3) {
                isExploitSkull(tag);
            }
        } catch (Exception e) {
        }
        return false;
    }
    
    public boolean isExploit(ItemStack stack, Player p) {
        if (stack == null || stack.getType() == Material.AIR) return false;
        if (this.world.contains(p.getWorld().getName().toLowerCase()) || plugin.isMagicItem(stack)) return false;
        if (this.checkNbt(stack, p)) return true;
        return checkEnchants(stack, p);
    }

    private boolean isCrashItem(ItemStack stack, NbtCompound tag, Material mat) {
        if (stack.getAmount() <1 || stack.getAmount() > 64 || tag.getKeys().size() > 20) {
            return true;
        }
        int tagL = tag.toString().length();
        if ((mat == Material.NAME_TAG || tiles.contains(mat)) && tagL > 600) {
            return true;
        }
        return mat == Material.WRITTEN_BOOK ? (tagL >= 22000) : (tagL >= 13000);
    }
    
    private NbtCompound getClearEntityTag(NbtCompound enttag) {
        String id = enttag.getString("id");
        enttag.getKeys().clear();
        enttag.put("id",id);
        return enttag;
    }
}