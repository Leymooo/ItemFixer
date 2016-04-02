package ru.xtime_1_9R1;

import java.util.Map;

import net.minecraft.server.v1_9_R1.NBTTagCompound;

import org.bukkit.Material;
import org.bukkit.craftbukkit.v1_9_R1.inventory.CraftItemStack;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

public class Checks
{
    public static boolean removeEnt(final ItemStack item) {
        boolean b = false;
        if (item != null) {
        for (final Map.Entry<Enchantment, Integer> ench : item.getEnchantments().entrySet()) {
            final Enchantment Enchant = ench.getKey();
            if (ench.getValue() > Enchant.getMaxLevel() || ench.getValue() < 0) {
                final ItemMeta meta = item.getItemMeta();
                meta.removeEnchant(Enchant);
                item.setItemMeta(meta);
                b = true;
            }
        }
        }
        return b;
    }
    
    public static boolean checkAttributes(final ItemStack item) {
        final CraftItemStack craft = getCraftVersion(item);
        final NBTTagCompound NBT = new NBTTagCompound();
        if (craft != null && item.getType() != Material.AIR && item != null) {
            CraftItemStack.asNMSCopy((ItemStack)craft).save(NBT);
            if (NBT.toString().contains("AttributeModifiers:") || NBT.toString().contains("Unbreakable:") || NBT.toString().contains("ClickEvent") || NBT.toString().contains("run_command") || NBT.toString().contains("CustomPotionEffects:")) {
                return true;
            }
        } if (craft != null && item.getType() == Material.ARMOR_STAND && item != null) {
            CraftItemStack.asNMSCopy((ItemStack)craft).save(NBT);
            if (NBT.toString().contains("EntityTag:")) {
                return true;
            }
        }
        return false;
    }
    
    
    private static CraftItemStack getCraftVersion(final ItemStack stack) {
        if (stack instanceof CraftItemStack) {
            return (CraftItemStack)stack;
        }
        if (stack != null) {
            return CraftItemStack.asCraftCopy(stack);
        }
        return null;
    }
}
