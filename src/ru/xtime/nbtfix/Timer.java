package ru.xtime.nbtfix;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;



public class Timer implements Runnable {
    private final Main plugin;
    
    Boolean CheckInventory;
    Boolean CheckArmor;
    
    public Timer(Main Main) {
        this.plugin = Main;
        CheckArmor = plugin.getConfig().getBoolean("Timer.CheckArmor");
        CheckInventory =  plugin.getConfig().getBoolean("Timer.CheckInventory");
    }

    @Override
    public void run() {
        for (Player p : Bukkit.getOnlinePlayers()) {
            if (CheckInventory) {
                for (ItemStack it : p.getInventory().getContents()) {
                    if (it != null && it.getType() != Material.AIR) { 
                        if (plugin.isExploit(it, p.getWorld().getName().toLowerCase())) {
                            p.updateInventory();
                        }
                    }
                }
            }
            if (CheckArmor) {
                for (ItemStack it : p.getInventory().getArmorContents()) {
                    if (it != null && it.getType() != Material.AIR) { 
                        if (plugin.isExploit(it, p.getWorld().getName().toLowerCase())) {
                            p.updateInventory();
                        }
                    }
                }
            }
        }
    }
}
