package ru.xtime_1_7_R4;

import org.bukkit.Material;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.entity.ThrownPotion;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockDispenseEvent;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.ProjectileLaunchEvent;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.player.PlayerDropItemEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerItemConsumeEvent;
import org.bukkit.event.player.PlayerPickupItemEvent;
import org.bukkit.potion.PotionEffect;

public class XListener implements Listener{  
	@EventHandler(priority = EventPriority.LOWEST, ignoreCancelled = true)
	public void onDamage(EntityDamageByEntityEvent event){
		if (event.getDamager().getType() == EntityType.PLAYER) {
			Player player2 = (Player) event.getDamager();
			if (!player2.hasPermission("itemfixer.bypass")) {
				event.setCancelled(Checks.checkAttributes(player2.getInventory().getItemInHand()));
				if (Checks.removeEnt(player2.getInventory().getItemInHand())) {
					event.setCancelled(true);
				}
			}
		}
	}

	@EventHandler(priority = EventPriority.LOWEST, ignoreCancelled = true)
	public void onPickupItem(final PlayerPickupItemEvent e) {
		if (!e.getPlayer().hasPermission("itemfixer.bypass")) {
			e.setCancelled(Checks.checkAttributes(e.getItem().getItemStack()));
			if (Checks.removeEnt(e.getItem().getItemStack())){
				e.setCancelled(true);
			}
		}
	}

	@EventHandler(priority = EventPriority.LOWEST, ignoreCancelled = true)
	public void InventoryClick(final InventoryClickEvent e) {
		if (e.getCurrentItem() != null) {
			if (!e.getWhoClicked().hasPermission("itemfixer.bypass")) {
				e.setCancelled(Checks.checkAttributes(e.getCurrentItem()));
				if (Checks.removeEnt(e.getCurrentItem())) {
					e.setCancelled(true);
				}
			}
		}
	}

	@EventHandler(priority = EventPriority.LOWEST, ignoreCancelled = true)
	public void onPlayerInteract(final PlayerInteractEvent e) {
		if (!e.getPlayer().hasPermission("itemfixer.bypass") && e.getItem() != null) {
			e.setCancelled(Checks.checkAttributes(e.getItem()));
			if (Checks.removeEnt(e.getItem())) {
				e.setCancelled(true);
			}
		}
	}

	@EventHandler(priority = EventPriority.LOWEST, ignoreCancelled = true)
	public void onItemDrop(final PlayerDropItemEvent e) {
		if (!e.getPlayer().hasPermission("itemfixer.bypass")) {
			e.setCancelled(Checks.checkAttributes(e.getItemDrop().getItemStack()));
			if (Checks.removeEnt(e.getItemDrop().getItemStack())) {
				e.setCancelled(true);
			}
		}
	}

	@EventHandler(priority = EventPriority.LOWEST, ignoreCancelled = true)
	public void OnUse(PlayerItemConsumeEvent e ){
		if (!e.getPlayer().hasPermission("itemfixer.bypass")) {
			if (e.getItem().getType() == Material.POTION) {
				e.setCancelled(Checks.checkAttributes(e.getItem()));
				if (Checks.removeEnt(e.getItem())) {
					e.setCancelled(true);
				}
			}
		}
	}
	@EventHandler(priority = EventPriority.LOWEST, ignoreCancelled = true)
	public void OnLaunch2 (BlockDispenseEvent e){
		e.setCancelled(Checks.checkAttributes(e.getItem()));
		if (Checks.removeEnt(e.getItem())) {
			e.setCancelled(true);
		}
	}
	@EventHandler(priority = EventPriority.LOWEST, ignoreCancelled = true)
	public void OnLaunch (ProjectileLaunchEvent e){
		if (e.getEntityType() == EntityType.SPLASH_POTION && e.getEntity().getShooter() instanceof Player) {
			Player pl = (Player) e.getEntity().getShooter();
			if (!pl.hasPermission("itemfixer.bypass")) {
				ThrownPotion p = (ThrownPotion) e.getEntity();
				for (PotionEffect pe : p.getEffects()) {
					if (pe.getAmplifier() > 1 || pe.getDuration() > 9600) {
						e.setCancelled(true);
					}
				}
			}
		}
	}
}

