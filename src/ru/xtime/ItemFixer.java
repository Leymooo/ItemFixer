// 
// Decompiled by Procyon v0.5.30
// 

package ru.xtime;

import org.bukkit.Bukkit;
import org.bukkit.event.Listener;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.java.JavaPlugin;

public class ItemFixer extends JavaPlugin {
    private String ItemFixer;

	    public void onEnable() {
	    	
	        if (setupItemFixer()) {


	            getLogger().info("ItemFixer setup was successful!// Плагин запущен");
	            getLogger().info("The plugin setup process is complete! // Плагин запущен");

	        } else {

	            getLogger().severe("Failed to start plugin//Ошибка при включении плагина!");
	            getLogger().severe("Your server version is not compatible with this plugin//Ваша версия сервера не поддерживается!");

	            Bukkit.getPluginManager().disablePlugin(this);
	        }
	    }
	    	
		
private boolean setupItemFixer() {

    String version;

    try {

        version = Bukkit.getServer().getClass().getPackage().getName().replace(".",  ",").split(",")[3];

    } catch (ArrayIndexOutOfBoundsException whatVersionAreYouUsingException) {
        return false;
    }

    getLogger().info("Server version " + version);

    if (version.equals("v1_9_R1")) {
    	ItemFixer = "Support";
        this.getServer().getPluginManager().registerEvents((Listener)new ru.xtime_1_9R1.XListener(), (Plugin)this);
       Bukkit.getScheduler().runTaskTimerAsynchronously((Plugin)this, (Runnable)new ru.xtime_1_9R1.Task5(), 20L, 20L);
       getLogger().severe("1.9");
    } else if (version.equals("v1_8_R3")) {
    	ItemFixer = "Support";
        this.getServer().getPluginManager().registerEvents((Listener)new ru.xtime_1_8_R3.XListener(), (Plugin)this);
        Bukkit.getScheduler().runTaskTimerAsynchronously((Plugin)this, (Runnable)new ru.xtime_1_8_R3.Task5(), 20L, 20L);
        getLogger().severe("1.8R3");
    }else if (version.equals("v1_8_R2")) {
    	ItemFixer = "Support";
        this.getServer().getPluginManager().registerEvents((Listener)new ru.xtime_1_8_R2.XListener(), (Plugin)this);
       Bukkit.getScheduler().runTaskTimerAsynchronously((Plugin)this, (Runnable)new ru.xtime_1_8_R2.Task5(), 20L, 20L);
       getLogger().severe("1.8R2");
    }else if (version.equals("v1_8_R1")) {
    	ItemFixer = "Support";
        this.getServer().getPluginManager().registerEvents((Listener)new ru.xtime_1_8_R1.XListener(), (Plugin)this);
       Bukkit.getScheduler().runTaskTimerAsynchronously((Plugin)this, (Runnable)new ru.xtime_1_8_R1.Task5(), 20L, 20L);
       getLogger().severe("1.8R1");
    }else if (version.equals("v1_7_R4")) {
    	ItemFixer = "Support";
        this.getServer().getPluginManager().registerEvents((Listener)new ru.xtime_1_7_R4.XListener(), (Plugin)this);
       Bukkit.getScheduler().runTaskTimerAsynchronously((Plugin)this, (Runnable)new ru.xtime_1_7_R4.Task5(), 20L, 20L);
       getLogger().severe("1.7.10");
    }
    
    
    return ItemFixer != null;
}

	}
