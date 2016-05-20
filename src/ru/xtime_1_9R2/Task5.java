package ru.xtime_1_9R2;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

public class Task5 implements Runnable {
	public void run() {
		for (final Player p : Bukkit.getServer().getOnlinePlayers()) {
			if (!p.hasPermission("itemfixer.bypass")) {
				for (final PotionEffect pe : p.getActivePotionEffects()) {
					if (pe.getType().equals((Object)PotionEffectType.ABSORPTION)) {
						if (pe.getAmplifier() <= 4) {
							continue;
						}	
						p.removePotionEffect(pe.getType());
					}
					else {
						if (pe.getAmplifier() <= 1) {
							continue;
						}
						p.removePotionEffect(pe.getType());
					}
				}
				for (ItemStack it : p.getInventory().getStorageContents()) {
					if (it != null) {
						final boolean a = ru.xtime_1_9R2.Checks.checkAttributes(it);
						if (a) {
							p.getInventory().remove(it);
						}
						ru.xtime_1_9R2.Checks.removeEnt(it);
					}
				}
				for (ItemStack it : p.getInventory().getExtraContents()) {
					if (it != null) {
						final boolean a = ru.xtime_1_9R2.Checks.checkAttributes(it);
						if (a) {
							p.getInventory().setExtraContents(null);
						}
						ru.xtime_1_9R2.Checks.removeEnt(it);
					}
				}
				for (ItemStack it : p.getInventory().getArmorContents()) {
					if (it != null) {
						final boolean a = ru.xtime_1_9R2.Checks.checkAttributes(it);
						if (a) {
							p.getInventory().setArmorContents(null);
						}
						ru.xtime_1_9R2.Checks.removeEnt(it);
					}
				}
			}
		}
	}
}