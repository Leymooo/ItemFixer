package ru.xtime.nbtfix;

import org.bukkit.Material;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
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
            event.getPlayer().sendMessage("§cЧитерские вещи запрещены! Если вы продолжите, вы будете забанены!");
        }
    }
        
        
}
