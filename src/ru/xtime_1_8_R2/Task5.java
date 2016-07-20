package ru.xtime_1_8_R2;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

public class Task5 implements Runnable {
	@Override
	public void run() {
		for (final Player p : Bukkit.getServer().getOnlinePlayers()) {
			if (!p.hasPermission("itemfixer.bypass")) {
				for (final PotionEffect pe : p.getActivePotionEffects()) {
					if (pe.getType().equals(PotionEffectType.REGENERATION)) {
						if (pe.getAmplifier() <= 4) {
							continue;
						}
						p.removePotionEffect(pe.getType());
					} else {
						if (pe.getAmplifier() <= 1) {
							continue;
						}
						p.removePotionEffect(pe.getType());
					}
				}
				for (ItemStack it : p.getInventory().getContents()) {
					if (it != null) {
						final boolean a = Checks.checkAttributes(it);
						if (a) {
							p.getInventory().remove(it);
						}
						Checks.removeEnt(it);
					}
				}
				for (ItemStack it2 : p.getInventory().getArmorContents()) {
					if (it2 !=null) {
						final boolean a = Checks.checkAttributes(it2);
						if (a) {
							p.getEquipment().setArmorContents(null);
						}
						Checks.removeEnt(it2);
					}
				}
			}
		}
	}
}