package ru.xtime;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URL;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.ConsoleCommandSender;

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
				b = true;
			    String message = ChatColor.translateAlternateColorCodes('&', "&b[ItemFixer] &cНайдено новое обновление! &7// &cNew update found");
			    String message2 = ChatColor.translateAlternateColorCodes('&', "&b[ItemFixer] &chttp://rubukkit.org/threads/119485/");
			    String message3 = ChatColor.translateAlternateColorCodes('&', "&b[ItemFixer] &eПытаюсь поставить обновление &7// &e Try to update the plugin");
			    ConsoleCommandSender console = Bukkit.getServer().getConsoleSender();
			    console.sendMessage(message);
			    console.sendMessage(message2);
			    console.sendMessage(message3);
				plugin.Update();
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
			String message = ChatColor.translateAlternateColorCodes('&', "&b[ItemFixer] &4Не удалось установить соединение с сервером проверки версии &e:( &7// &4Can't connect to server");
			ConsoleCommandSender console = Bukkit.getServer().getConsoleSender();
			console.sendMessage(message);
			return false;
		} catch (IOException e) {
			String message = ChatColor.translateAlternateColorCodes('&', "&b[ItemFixer] &4Не удалось установить соединение с сервером проверки версии &e:( &7// &4Can't connect to server");
			ConsoleCommandSender console = Bukkit.getServer().getConsoleSender();
			console.sendMessage(message);
			return false;
		}
	}
}