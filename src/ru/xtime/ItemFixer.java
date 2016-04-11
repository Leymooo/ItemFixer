package ru.xtime;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.event.Listener;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.java.JavaPlugin;

public class ItemFixer extends JavaPlugin {
	private String ItemFixer;
    static ItemFixer plugin;

	public void onEnable() {
		plugin = this;
		if (setupItemFixer()) {
			Bukkit.getScheduler().runTaskTimerAsynchronously((Plugin)this, (Runnable)new UpdateChecker(), 6000L, 6000L);
		    String message = ChatColor.translateAlternateColorCodes('&', "&aПлагин включен! &7// &aPlugin enabled");
		    ConsoleCommandSender console = Bukkit.getServer().getConsoleSender();
		    console.sendMessage(message);
		} else {
		    String message = ChatColor.translateAlternateColorCodes('&', "&aВерсия не поддерживается. &7// &aServer version is not supported");
		    ConsoleCommandSender console = Bukkit.getServer().getConsoleSender();
		    console.sendMessage(message);
			Bukkit.getPluginManager().disablePlugin(this);
		}
		if (UpdateChecker.isUpdate()) {
		    String message = ChatColor.translateAlternateColorCodes('&', "&aЕсть обновление. &chttp://rubukkit.org/threads/119485/ &7// &aNew update found");
		    ConsoleCommandSender console = Bukkit.getServer().getConsoleSender();
		    console.sendMessage(message);
		} else {
		    String message = ChatColor.translateAlternateColorCodes('&', "&aОбновление не найдено! &7// &aUpdate not found");
		    ConsoleCommandSender console = Bukkit.getServer().getConsoleSender();
		    console.sendMessage(message);
		}
	}	
	private boolean setupItemFixer() {
		String version;
		try {
			version = Bukkit.getServer().getClass().getPackage().getName().replace(".",  ",").split(",")[3];
		} catch (ArrayIndexOutOfBoundsException whatVersionAreYouUsingException) {
			return false;
		}
	    String message = ChatColor.translateAlternateColorCodes('&', "&aServer version &c" + version);
	    ConsoleCommandSender console = Bukkit.getServer().getConsoleSender();
	    console.sendMessage(message);
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
