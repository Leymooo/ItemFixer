package ru.leymooo.fixer;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Map;

import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import com.comphenix.protocol.wrappers.nbt.NbtCompound;
import com.comphenix.protocol.wrappers.nbt.NbtFactory;
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
        ignoreNbt.addAll(plugin.getConfig().getStringList("ignore-tags"));
        nbt.addAll(Arrays.asList("ActiveEffects", "Command", "CustomName", "AttributeModifiers", "Unbreakable", "CustomPotionEffects"));
        nbt.removeAll(ignoreNbt);
        tiles.addAll(Arrays.asList(
                Material.FURNACE,Material.CHEST, Material.DROPPER, Material.DISPENSER, Material.COMMAND, Material.COMMAND_MINECART, Material.HOPPER_MINECART,
                Material.HOPPER, Material.BREWING_STAND_ITEM, Material.BEACON, Material.SIGN, Material.MOB_SPAWNER, Material.NOTE_BLOCK));
        for (String w : plugin.getConfig().getStringList("ignore-worlds")) {
            world.add(w.toLowerCase());
        }
        checkench = plugin.getConfig().getBoolean("check-enchants");
        removeInvalidEnch = plugin.getConfig().getBoolean("remove-invalid-enchants");
    }
    public void isExploitSkull(NbtCompound root) {
        String tagS = root.toString();
        if(tagS.contains("SkullOwner:") && tagS.contains("Properties:") && tagS.contains("textures:") && tagS.contains("Value:")) {
            String decoded = null;
            try {
                decoded = new String(BaseEncoding.base64().decode(tagS.split("Value:")[1].split("}]},")[0]));
            } catch (Exception e) {
                root.remove("SkullOwner");
                return;
            }
            if (decoded.contains("textures") && decoded.contains("SKIN")) {
                if (decoded.contains("url")) {
                    String Url = decoded.split("url\":")[1].replace("}", "").replace("\"", "");
                    if (!Url.startsWith("http://textures.minecraft.net/texture/")) {
                        root.remove("SkullOwner");
                    }
                } else {
                    root.remove("SkullOwner");
                }
            } else {
                root.remove("SkullOwner");
            }
        }
    }
    //
    private ItemMeta getClearItemMeta(ItemStack stack) {
        final ItemMeta meta = stack.getItemMeta();
        for (Map.Entry<Enchantment, Integer> ench : meta.getEnchants().entrySet()) {
            Enchantment Enchant = ench.getKey();
            if (removeInvalidEnch && !Enchant.canEnchantItem(stack) ) {
                meta.removeEnchant(Enchant);
            }
            if (ench.getValue() > Enchant.getMaxLevel() || ench.getValue() < 0) {
                meta.removeEnchant(Enchant);
            }
        }
        return meta;
    }
    public boolean isExploit(ItemStack stack, String world) {
        if (stack == null || stack.getType() == Material.AIR) return false;
        if (this.world.contains(world.toLowerCase()) || plugin.isMagicItem(stack)) {
            return false;
        }
        try {
            Material mat = stack.getType();
            NbtCompound tag = (NbtCompound) NbtFactory.fromItemTag(stack);
            if(isCrashItem(stack, tag, mat)) {
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
        if (checkench && stack.hasItemMeta() && stack.getItemMeta().hasEnchants()) {
            stack.setItemMeta(getClearItemMeta(stack));
        }
        return false;
    }
    private boolean isCrashItem(ItemStack stack, NbtCompound tag, Material mat) {
        return (stack.getAmount() <1 || stack.getAmount()>64 || tag.getKeys().size() > 15 || tag.toString().length() > 12000) || 
                ((mat == Material.NAME_TAG || tiles.contains(mat) && tag.toString().length() > 600));
    }
    private NbtCompound getClearEntityTag(NbtCompound enttag) {
        String id = enttag.getString("id");
        enttag.getKeys().clear();
        enttag.put("id",id);
        return enttag;
    }
}
