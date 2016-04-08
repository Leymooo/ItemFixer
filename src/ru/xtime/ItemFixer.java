package ru.xtime;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URL;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.event.Listener;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.java.JavaPlugin;

public class ItemFixer extends JavaPlugin {
	private String ItemFixer;
	
	public void onEnable() {
		if (setupItemFixer()) {
		    String message = ChatColor.translateAlternateColorCodes('&', "&aПлагин включен! &7// &aPlugin enabled");
		    ConsoleCommandSender console = Bukkit.getServer().getConsoleSender();
		    console.sendMessage(message);
		} else {
		    String message = ChatColor.translateAlternateColorCodes('&', "&aВерсия не поддерживается. &7// &aServer version is not supported");
		    ConsoleCommandSender console = Bukkit.getServer().getConsoleSender();
		    console.sendMessage(message);
			Bukkit.getPluginManager().disablePlugin(this);
		}
		if (isUpdate()) {
			Bukkit.getScheduler().runTaskTimerAsynchronously((Plugin)this, (Runnable)new ru.xtime.Task6(), 36000L, 36000L);
		    String message = ChatColor.translateAlternateColorCodes('&', "&aЕсть обновление. &chttp://rubukkit.org/threads/119485/ &7// &aNew update found");
		    ConsoleCommandSender console = Bukkit.getServer().getConsoleSender();
		    console.sendMessage(message);
		} else {
		    String message = ChatColor.translateAlternateColorCodes('&', "&aОбновление не найдено! &7// &aUpdate not found");
		    ConsoleCommandSender console = Bukkit.getServer().getConsoleSender();
		    console.sendMessage(message);
		}
	}	
	public boolean isUpdate() {
		try {
	        String sourceLine = null;
	        
	        URL address;
				address = new URL("http://151.80.108.152/version.txt");

				InputStreamReader pageInput;
			try {
			pageInput = new InputStreamReader(address.openStream());
			} catch (IOException e1) {
			    String message = ChatColor.translateAlternateColorCodes('&', "&4Не удалось установить соединение с сервером проверки версии &e:( &7// &4Can't connect to server");
			    ConsoleCommandSender console = Bukkit.getServer().getConsoleSender();
			    console.sendMessage(message);
	        	return false;
			}
	        BufferedReader source = new BufferedReader(pageInput);
	        sourceLine = source.readLine();
	        Integer int1 = Integer.valueOf(sourceLine);
	        Integer version = Integer.valueOf(Bukkit.getPluginManager().getPlugin("ItemFixer").getDescription().getDescription().toString());
	        if(int1 > version){
	            return true;
	        } else {
	        	return false;

	    }
		} catch (MalformedURLException e2) {
		    String message = ChatColor.translateAlternateColorCodes('&', "&4Не удалось установить соединение с сервером проверки версии &e:( &7// &4Can't connect to server");
		    ConsoleCommandSender console = Bukkit.getServer().getConsoleSender();
		    console.sendMessage(message);
		    return false;
		} catch (IOException e) {
		    String message = ChatColor.translateAlternateColorCodes('&', "&4Не удалось установить соединение с сервером проверки версии &e:( &7// &4Can't connect to server");
		    ConsoleCommandSender console = Bukkit.getServer().getConsoleSender();
		    console.sendMessage(message);
		    return false;
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
