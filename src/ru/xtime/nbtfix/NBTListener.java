package ru.xtime.nbtfix;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.player.PlayerDropItemEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerPickupItemEvent;

import com.gmail.filoghost.chestcommands.internal.MenuInventoryHolder;

public class NBTListener implements Listener {
    private final Main plugin;
    private Boolean cc;
    public NBTListener(Main Main) {
        this.plugin = Main;
        this.cc = Bukkit.getPluginManager().getPlugin("ChestCommands") != null;
    }
    @EventHandler(priority = EventPriority.LOWEST, ignoreCancelled = false)
    public void onInteract(PlayerInteractEvent event) {
        final Player p = event.getPlayer();
        if (p.hasPermission("itemfixer.bypass")) return;
        if (event.getItem() == null) return;
        if (plugin.mc19 && event.getItem().getType() == Material.STRUCTURE_BLOCK) {
            p.getInventory().remove(event.getItem());
            event.setCancelled(true);
        }
        if (plugin.isExploit(event.getItem(), p.getWorld().getName().toLowerCase())) {
            event.setCancelled(true);
            p.updateInventory();
            p.sendMessage("§cЧитерские вещи запрещены! Если вы продолжите, вы будете забанены!");
        }
    }
    @EventHandler(priority = EventPriority.LOWEST, ignoreCancelled = false)
    public void onInvClick(InventoryClickEvent event) {
        if (cc && event.getInventory().getHolder() instanceof MenuInventoryHolder) return;
        if (event.getWhoClicked().getType() != EntityType.PLAYER) return;
        final Player p = (Player) event.getWhoClicked();
        if (p.hasPermission("itemfixer.bypass")) return;
        if (event.getCurrentItem() == null) return;
        if (plugin.isExploit(event.getCurrentItem(), p.getWorld().getName().toLowerCase())) {
            event.setCancelled(true);
            p.updateInventory();
            p.sendMessage("§cЧитерские вещи запрещены! Если вы продолжите, вы будете забанены!");
        }
    }
    @EventHandler(priority = EventPriority.LOWEST, ignoreCancelled = false)
    public void onDrop(PlayerDropItemEvent event) {
        final Player p = event.getPlayer();
        if (p.hasPermission("itemfixer.bypass")) return;
        if (event.getItemDrop() == null) return;
        if (plugin.mc19 && event.getItemDrop().getItemStack().getType() == Material.STRUCTURE_BLOCK) {
            event.getItemDrop().remove();
        }
        if (plugin.isExploit(event.getItemDrop().getItemStack(), p.getWorld().getName().toLowerCase())) {
            event.setCancelled(true);
            p.updateInventory();
            p.sendMessage("§cЧитерские вещи запрещены! Если вы продолжите, вы будете забанены!");
        }
    }
    @EventHandler(priority = EventPriority.LOWEST, ignoreCancelled = false)
    public void onPickup(PlayerPickupItemEvent event) {
        final Player p = event.getPlayer();
        if (p.hasPermission("itemfixer.bypass")) return;
        if (event.getItem() == null) return;
        if (plugin.mc19 && event.getItem().getItemStack().getType() == Material.STRUCTURE_BLOCK) {
            event.getItem().remove();
        }
        if (plugin.isExploit(event.getItem().getItemStack(), p.getWorld().getName().toLowerCase())) {
            event.setCancelled(true);
            p.updateInventory();
            p.sendMessage("§cЧитерские вещи запрещены! Если вы продолжите, вы будете забанены!");
        }
    }

}
