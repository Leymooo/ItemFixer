package ru.xtime.nbtfix;

import org.bukkit.Bukkit;
import org.bukkit.Material;
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
                    if (it != null && it.getType() != Material.AIR) { 
                        if (plugin.isExploit(it)) {
                            p.updateInventory();
                        }
                    }
                }
            }
            if (plugin.CheckArmor) {
                for (ItemStack it : p.getInventory().getArmorContents()) {
                    if (it != null && it.getType() != Material.AIR) { 
                        if (plugin.isExploit(it)) {
                            p.updateInventory();
                        }
                    }
                }
            }
        }
    }
}
