package ru.leymooo.fixer;


import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.player.PlayerDropItemEvent;
import org.bukkit.event.player.PlayerItemHeldEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

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
        if (cc && event.getInventory().getHolder() != null && event.getInventory().getHolder().getClass().getSimpleName().equals(
                "MenuInventoryHolder"))
            return;
        if (event.getWhoClicked().getType() != EntityType.PLAYER) return;
        final Player p = (Player) event.getWhoClicked();
        if (event.getCurrentItem() == null) return;
        ItemChecker.CheckStatus checkStatus = plugin.checkItem(event.getCurrentItem(), p);
        if (checkStatus == ItemChecker.CheckStatus.FAILED) {
            removeItemFromInventory(event.getClickedInventory(), event.getCurrentItem());
            removeItemFromInventory(event.getInventory(), event.getCurrentItem());
            event.setCurrentItem(null);
            event.setCancelled(true);
            return;
        }
        if (checkStatus != ItemChecker.CheckStatus.GOOD) {
            event.setCancelled(true);
            p.updateInventory();
        }
    }

    @EventHandler(priority = EventPriority.LOWEST, ignoreCancelled = false)
    public void onDrop(PlayerDropItemEvent event) {
        final Player p = event.getPlayer();
        if (event.getItemDrop() == null) return;
        ItemChecker.CheckStatus checkStatus = plugin.checkItem(event.getItemDrop().getItemStack(), p);
        if (checkStatus == ItemChecker.CheckStatus.FAILED) {
            event.getItemDrop().remove();
            return;
        }
        if (checkStatus != ItemChecker.CheckStatus.GOOD) {
            event.setCancelled(true);
            p.updateInventory();
        }
    }

    @EventHandler(priority = EventPriority.LOWEST, ignoreCancelled = false)
    public void onSlotChange(PlayerItemHeldEvent event) {
        Player p = event.getPlayer();
        ItemStack stack = p.getInventory().getItem(event.getNewSlot());
        ItemChecker.CheckStatus checkStatus = plugin.checkItem(stack, p);
        if (checkStatus == ItemChecker.CheckStatus.FAILED) {
            p.getInventory().setItem(event.getNewSlot(), null);
            event.setCancelled(true);
            return;
        }
        if (plugin.checkItem(stack, p) != ItemChecker.CheckStatus.GOOD) {
            event.setCancelled(true);
            p.updateInventory();
        }
    }

    /* remove this for now
    @EventHandler
    public void onJoin(PlayerJoinEvent event) {
        for (ItemStack stack : event.getPlayer().getInventory().getContents()) {
            plugin.checkItem(stack, event.getPlayer());
        }
    }
*/
    @EventHandler
    public void onQuit(PlayerQuitEvent event) {
        NBTListener.cancel.invalidate(event.getPlayer());
    }

    private void removeItemFromInventory(Inventory inv, ItemStack stack) {
        if (inv != null && stack != null) {
            inv.remove(stack);
        }
    }
}
