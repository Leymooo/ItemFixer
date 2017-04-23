package ru.leymooo.fixer;


import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.player.PlayerDropItemEvent;
import org.bukkit.event.player.PlayerPickupItemEvent;

import com.gmail.filoghost.chestcommands.internal.MenuInventoryHolder;

public class NBTBukkitListener implements Listener {
    private final Main plugin;
    private Boolean cc;
    public NBTBukkitListener(Main Main) {
        this.plugin = Main;
        try {
            Class.forName("com.gmail.filoghost.chestcommands.internal.MenuInventoryHolder");
            cc = true;
        } catch (ClassNotFoundException e) {
            cc = false;
        }
    }
    @EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
    public void onInvClick(InventoryClickEvent event) {
        if (cc && event.getInventory().getHolder() instanceof MenuInventoryHolder) return;
        if (event.getWhoClicked().getType() != EntityType.PLAYER) return;
        final Player p = (Player) event.getWhoClicked();
        if (p.hasPermission("itemfixer.bypass")) return;
        if (event.getCurrentItem() == null) return;
        if (plugin.checkItem(event.getCurrentItem(), p.getWorld().getName().toLowerCase())) {
            event.setCancelled(true);
            p.updateInventory();
        }
    }
    @EventHandler(priority = EventPriority.LOWEST, ignoreCancelled = false)
    public void onDrop(PlayerDropItemEvent event) {
        final Player p = event.getPlayer();
        if (p.hasPermission("itemfixer.bypass")) return;
        if (event.getItemDrop() == null) return;
        if (plugin.checkItem(event.getItemDrop().getItemStack(), p.getWorld().getName().toLowerCase())) {
            event.setCancelled(true);
            p.updateInventory();
        }
    }
    @EventHandler(priority = EventPriority.LOWEST, ignoreCancelled = false)
    public void onPickup(PlayerPickupItemEvent event) {
        final Player p = event.getPlayer();
        if (p.hasPermission("itemfixer.bypass")) return;
        if (event.getItem() == null) return;
        if (plugin.checkItem(event.getItem().getItemStack(), p.getWorld().getName().toLowerCase())) {
            event.getItem().remove();
            event.setCancelled(true);
        }
    }
}
