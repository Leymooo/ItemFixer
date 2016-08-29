package ru.xtime.nbtfix;

import org.bukkit.Material;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.player.PlayerDropItemEvent;
import org.bukkit.event.player.PlayerInteractEvent;

public class NBTInteractListener implements Listener {
    private final Main plugin;

    public NBTInteractListener(Main Main) {
        this.plugin = Main;
    }
    @EventHandler(priority = EventPriority.LOWEST, ignoreCancelled = false)
    public void onInteract(PlayerInteractEvent event) {
        if (event.getPlayer().hasPermission("itemfixer.bypass")) return;
        if (event.getItem() == null) return;
        if (plugin.mc19 && event.getItem().getType() == Material.STRUCTURE_BLOCK) {
            event.getPlayer().getInventory().remove(event.getItem());
            event.setCancelled(true);
        }
        if (plugin.isExploit(event.getItem())) {
            event.setCancelled(true);
            event.getPlayer().updateInventory();
            event.getPlayer().sendMessage("§cЧитерские вещи запрещены! Если вы продолжите, вы будете забанены!");
        }
    }
    @EventHandler(priority = EventPriority.LOWEST, ignoreCancelled = false)
    public void onInvClick(InventoryClickEvent event) {
        if (event.getWhoClicked().getType() != EntityType.PLAYER) return;
        if (event.getWhoClicked().hasPermission("itemfixer.bypass")) return;
        if (event.getCurrentItem() == null) return;
        if (plugin.isExploit(event.getCurrentItem())) {
            event.setCancelled(true);
            ((Player) event.getWhoClicked()).updateInventory();
            event.getWhoClicked().sendMessage("§cЧитерские вещи запрещены! Если вы продолжите, вы будете забанены!");
        }
    }
    @EventHandler(priority = EventPriority.LOWEST, ignoreCancelled = false)
    public void onDrop(PlayerDropItemEvent event) {
        if (event.getPlayer().hasPermission("itemfixer.bypass")) return;
        if (event.getItemDrop() == null) return;
        if (plugin.mc19 && event.getItemDrop().getItemStack().getType() == Material.STRUCTURE_BLOCK) {
            event.getItemDrop().remove();
        }
        if (plugin.isExploit(event.getItemDrop().getItemStack())) {
            event.setCancelled(true);
            event.getPlayer().updateInventory();
            event.getPlayer().sendMessage("§cЧитерские вещи запрещены! Если вы продолжите, вы будете забанены!");
        }
    }

}
