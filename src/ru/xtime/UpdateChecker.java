package ru.xtime;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URL;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.plugin.Plugin;

public class UpdateChecker implements Runnable {
	Boolean b;
    ItemFixer plugin;
    public UpdateChecker() {
        this.plugin = ItemFixer.plugin;
        b = false;
    }
	public void run() {
		if (!b){
			if (isUpdate()) {
				Bukkit.getScheduler().runTaskTimerAsynchronously((Plugin)this.plugin, (Runnable)new UpdNotification(), 24000L, 24000L);
				b = true;
			}
		}
	}
	public static boolean isUpdate() {
		try {
			String sourceLine = null;
			
			URL address;
			address = new URL("http://151.80.108.152/version.txt");
			
			InputStreamReader pageInput;
			pageInput = new InputStreamReader(address.openStream());
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
}