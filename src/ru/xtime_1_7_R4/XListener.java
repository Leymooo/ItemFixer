package ru.xtime_1_7_R4;

import org.bukkit.Material;
import org.bukkit.entity.Item;
import org.bukkit.entity.Player;
import org.bukkit.entity.ThrownPotion;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.ProjectileLaunchEvent;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.player.PlayerDropItemEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerItemConsumeEvent;
import org.bukkit.event.player.PlayerPickupItemEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.PotionMeta;
import org.bukkit.potion.PotionEffect;

public class XListener implements Listener{  
    
	@EventHandler
	public void onPickupItem(final PlayerPickupItemEvent e) {
		if (e.isCancelled()) {
			return;
		}
		Player player = e.getPlayer();
		if (!player.hasPermission("itemfixer.bypass")) {
			final Item item = e.getItem();
			final boolean a = ru.xtime_1_7_R4.Checks.checkAttributes(item.getItemStack());
			if (a) {
				e.setCancelled(true);
				item.remove();
			}
			ru.xtime_1_7_R4.Checks.removeEnt(item.getItemStack());
		}
	}
    
	@EventHandler
	public void InventoryClick(final InventoryClickEvent e) {
		if (e.isCancelled()) {
			return;
		}
		if (e.getCurrentItem() != null) {
			Player player = (Player) e.getWhoClicked();
			if (!player.hasPermission("itemfixer.bypass")) {
				final ItemStack item = e.getCurrentItem();
				final boolean a = ru.xtime_1_7_R4.Checks.checkAttributes(item);
				if (a) {
					e.getWhoClicked().getInventory().remove(item);
					e.setCancelled(true);
				}
				ru.xtime_1_7_R4.Checks.removeEnt(item);
			}
		}
	}
    
	@EventHandler
	public void onPlayerInteract(final PlayerInteractEvent e) {
		if (e.isCancelled()) {
			return;
		}
		if (e.getItem() != null) {
			Player player = e.getPlayer();
			if (!player.hasPermission("itemfixer.bypass")) {
				final ItemStack item = e.getItem();
				final boolean a = ru.xtime_1_7_R4.Checks.checkAttributes(item);
				if (a) {
					e.getPlayer().getInventory().remove(item);
				}
				final boolean b = ru.xtime_1_7_R4.Checks.removeEnt(item);
				e.setCancelled(b);
			}
		}
	}
    
	@EventHandler
	public void onItemDrop(final PlayerDropItemEvent e) {
		if (e.isCancelled()) {
			return;
		}
		Player player = e.getPlayer();
		if (!player.hasPermission("itemfixer.bypass")) {
			final Item item = e.getItemDrop();
			final boolean a = ru.xtime_1_7_R4.Checks.checkAttributes(item.getItemStack());
			if (a) {
				e.setCancelled(true);
			}
			ru.xtime_1_7_R4.Checks.removeEnt(item.getItemStack());
		}
	}

	@EventHandler(priority = EventPriority.HIGHEST)
	public void OnUse(PlayerItemConsumeEvent e ){
		if (e.isCancelled()) {
			return;
		}
		Player player = e.getPlayer();
		if (!player.hasPermission("itemfixer.bypass")) {
			ItemStack hand = e.getItem();
			if (hand.getType() == Material.POTION) {
				final PotionMeta meta = (PotionMeta) e.getItem().getItemMeta();
				if (meta.hasCustomEffects()) {
					e.setCancelled(true);
					player.getInventory().remove(hand);
				}
			}
		}
	}
       
	@EventHandler(priority = EventPriority.HIGHEST)
	public void OnSpawn2 (ProjectileLaunchEvent e){
		if (e.isCancelled()) {
			return;
		}
		if (e.getEntity() instanceof ThrownPotion){
			ThrownPotion potion = (ThrownPotion) e.getEntity();
			for (PotionEffect pe : potion.getEffects()) {
				if (pe.getAmplifier() > 1) {
					e.setCancelled(true);
				}
			}
		}       
	}
}	

