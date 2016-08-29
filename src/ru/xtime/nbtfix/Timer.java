package ru.xtime.nbtfix;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;


public class Timer implements Runnable {

    private final Main plugin;

    public Timer(Main Main) {
        this.plugin = Main;
    }

    @Override
    public void run() {
        for (Player p : Bukkit.getOnlinePlayers()) {
            if (plugin.CheckInventory) {
                for (ItemStack it : p.getInventory().getContents()) {
                    plugin.isExploit(it);
                }
            }
            if (plugin.CheckArmor) {
                for (ItemStack it : p.getInventory().getArmorContents()) {
                    plugin.isExploit(it);
                }
            }
        }
    }
}
