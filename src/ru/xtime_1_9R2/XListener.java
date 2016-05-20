package ru.xtime_1_9R2;

import org.bukkit.Material;
import org.bukkit.entity.AreaEffectCloud;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Item;
import org.bukkit.entity.Player;
import org.bukkit.entity.TippedArrow;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockDispenseEvent;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.player.PlayerDropItemEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerItemConsumeEvent;
import org.bukkit.event.player.PlayerPickupItemEvent;
import org.bukkit.event.player.PlayerSwapHandItemsEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.PotionMeta;

public class XListener implements Listener{  
	@EventHandler(priority = EventPriority.LOWEST, ignoreCancelled = false)
	public void onDamage(EntityDamageByEntityEvent event){
		if(event.getEntity() instanceof Player) {
			if(event.getDamager().getType() == EntityType.TIPPED_ARROW){
				TippedArrow arrow = (TippedArrow) event.getDamager();
				if (arrow.getShooter() instanceof Player) {
					Player p =  (Player) arrow.getShooter();
					if (!p.hasPermission("itemfixer.bypass")) {
						if (arrow.hasCustomEffects()) {
							event.setCancelled(true);
							return;
						}
					}
				}
			}
			if (event.getDamager().getType() == EntityType.AREA_EFFECT_CLOUD) {
				AreaEffectCloud arc = (AreaEffectCloud) event.getDamager();
				if (arc.hasCustomEffects()) {
					event.setCancelled(true);
					arc.remove();
				}
			}
		}
		if (event.getDamager().getType() == EntityType.PLAYER) {
			Player player2 = (Player) event.getDamager();
			if (!player2.hasPermission("itemfixer.bypass")) {
				final ItemStack item = player2.getInventory().getItemInMainHand();
				boolean a = ru.xtime_1_9R2.Checks.checkAttributes(item);
				if (a) {
					event.setCancelled(a);
				}
			}
		}
	}
    
	@EventHandler(priority = EventPriority.LOWEST, ignoreCancelled = false)
	public void onPickupItem(final PlayerPickupItemEvent e) {
		Player player = e.getPlayer();
		if (!player.hasPermission("itemfixer.bypass")) {
			final Item item = e.getItem();
			final boolean a = ru.xtime_1_9R2.Checks.checkAttributes(item.getItemStack());
			if (a) {
				e.setCancelled(true);
				item.remove();
			}
			ru.xtime_1_9R2.Checks.removeEnt(item.getItemStack());
		}
	}
    
	@EventHandler(priority = EventPriority.LOWEST, ignoreCancelled = false)
	public void InventoryClick(final InventoryClickEvent e) {
		if (e.getCurrentItem() != null) {
			Player player = (Player) e.getWhoClicked();
			if (!player.hasPermission("itemfixer.bypass")) {
				final ItemStack item = e.getCurrentItem();
				final boolean a = ru.xtime_1_9R2.Checks.checkAttributes(item);
				if (a) {
					e.getWhoClicked().getInventory().remove(item);
					e.setCancelled(true);
				}
				ru.xtime_1_9R2.Checks.removeEnt(item);
			}
		}
	}
    
	@EventHandler(priority = EventPriority.LOWEST, ignoreCancelled = false)
	public void onPlayerInteract(final PlayerInteractEvent e) {
		Player player = e.getPlayer();
		if (!player.hasPermission("itemfixer.bypass") && e.getItem() != null) {
				final ItemStack item = e.getItem();
				final boolean a = ru.xtime_1_9R2.Checks.checkAttributes(item);
				if (a) {
					e.setCancelled(true);
				}
				ru.xtime_1_9R2.Checks.removeEnt(item);
		}
	}
    
	@EventHandler(priority = EventPriority.LOWEST, ignoreCancelled = false)
	public void onItemDrop(final PlayerDropItemEvent e) {
		Player player = e.getPlayer();
		if (!player.hasPermission("itemfixer.bypass")) {
			final Item item = e.getItemDrop();
			final boolean a = ru.xtime_1_9R2.Checks.checkAttributes(item.getItemStack());
			if (a) {
				e.setCancelled(true);
			}
			ru.xtime_1_9R2.Checks.removeEnt(item.getItemStack());
		}
	}

	@EventHandler(priority = EventPriority.LOWEST, ignoreCancelled = false)
	public void OnUse(PlayerItemConsumeEvent e ){
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
	@EventHandler(priority = EventPriority.LOWEST, ignoreCancelled = false)
	public void OnSwap(PlayerSwapHandItemsEvent e ){
		Player player = e.getPlayer();
		if (!player.hasPermission("itemfixer.bypass")) {
			ItemStack hand = e.getOffHandItem();
			final boolean a = ru.xtime_1_9R2.Checks.checkAttributes(hand);
			if (a) {
				e.setCancelled(true);
				player.getInventory().remove(hand);
			}
			ru.xtime_1_9R2.Checks.removeEnt(hand);
		}
	}
	@EventHandler(priority = EventPriority.LOWEST, ignoreCancelled = false)
	public void OnLaunch2 (BlockDispenseEvent e){
		final ItemStack item = e.getItem();
		final boolean a = ru.xtime_1_9R2.Checks.checkAttributes(item);
		if (a) {
			e.setCancelled(true);
		}
	}
}

