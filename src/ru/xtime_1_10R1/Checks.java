package ru.xtime_1_10R1;

import java.util.Map;

import net.minecraft.server.v1_10_R1.NBTTagCompound;

import org.bukkit.Material;
import org.bukkit.craftbukkit.v1_10_R1.inventory.CraftItemStack;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

public class Checks
{
	public static boolean removeEnt(final ItemStack item) {
		if (item != null) {
			if (item.getEnchantments() != null) {
				for (final Map.Entry<Enchantment, Integer> ench : item.getEnchantments().entrySet()) {
					final Enchantment Enchant = ench.getKey();
					if (ench.getValue() > Enchant.getMaxLevel() || ench.getValue() < 0) {
						final ItemMeta meta = item.getItemMeta();
						meta.removeEnchant(Enchant);
						item.setItemMeta(meta);
						return true;
					}
				}
			}
		}
		return false;
	}

	public static boolean checkAttributes(final ItemStack item) {
		final CraftItemStack craft = getCraftVersion(item);
		final NBTTagCompound NBT = new NBTTagCompound();
		if (craft != null && item.getType() != Material.AIR && item != null) {
			CraftItemStack.asNMSCopy((ItemStack)craft).save(NBT);
			final String NBTS = NBT.toString();
			if (NBTS.contains("Items:") || NBTS.contains("ActiveEffects:") || NBTS.contains("Command:") || NBTS.contains("powered:") || NBTS.contains("Equipment:") || NBTS.contains("Fuse:") || NBTS.contains("CustomName:") || NBTS.contains("AttributeModifiers:") || NBTS.contains("Unbreakable:") || NBTS.contains("ClickEvent") || NBTS.contains("run_command") || NBTS.contains("CustomPotionEffects:")) {
				return true;
			}
		} 
		if (craft != null && item.getType() == Material.ARMOR_STAND && item != null) {
			CraftItemStack.asNMSCopy((ItemStack)craft).save(NBT);
			final String NBTS = NBT.toString();
			if (NBTS.contains("EntityTag:")) {
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