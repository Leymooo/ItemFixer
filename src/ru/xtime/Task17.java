package ru.xtime;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

public class Task17 implements Runnable {
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
						Reflection.removeNbt(it);
					}
				}
				for (ItemStack it : p.getInventory().getArmorContents()) {
					if (it !=null) {
						Reflection.removeNbt(it);
					}
				}
			}
		}
	}
}