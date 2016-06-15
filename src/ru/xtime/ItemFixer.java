package ru.xtime;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.URL;
import java.util.logging.Level;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.plugin.java.JavaPlugin;

public class ItemFixer extends JavaPlugin implements Runnable {
	Boolean b;
	public void onEnable() {
		b = false;
		if (setupItemFixer()) {
			Bukkit.getScheduler().runTaskTimerAsynchronously(this, this, 18000L, 18000L);
			Bukkit.getConsoleSender().sendMessage(ChatColor.translateAlternateColorCodes('&', "&b[ItemFixer] &aПлагин включен! &7// &aPlugin enabled"));
		} else {
			Bukkit.getConsoleSender().sendMessage(ChatColor.translateAlternateColorCodes('&', "&b[ItemFixer] &aВерсия не поддерживается. &7// &aServer version is not supported"));
			Bukkit.getPluginManager().disablePlugin(this);
		}
		if (isUpdate()) {
			Bukkit.getConsoleSender().sendMessage(ChatColor.translateAlternateColorCodes('&', "&b[ItemFixer] &aЕсть обновление. &chttp:rubukkit.org/threads/119485/ &7// &aNew update found"));
		} else {
			Bukkit.getConsoleSender().sendMessage(ChatColor.translateAlternateColorCodes('&', "&b[ItemFixer] &aОбновление не найдено! &7// &aUpdate not found"));
		}
	}
	private boolean setupItemFixer() {
		String version = this.getServer().getClass().getPackage().getName().replace(".",  ",").split(",")[3];
		Bukkit.getConsoleSender().sendMessage(ChatColor.translateAlternateColorCodes('&', "&aServer version &c" + version));
		if (version.equals("v1_10_R1")) {
			Bukkit.getPluginManager().registerEvents(new ru.xtime_1_10R1.XListener(), this);
			Bukkit.getScheduler().runTaskTimerAsynchronously(this, new ru.xtime_1_10R1.Task5(), 20L, 20L);
			return true;
		} else if (version.equals("v1_9_R2")) {
			Bukkit.getPluginManager().registerEvents(new ru.xtime_1_9R2.XListener(), this);
			Bukkit.getScheduler().runTaskTimerAsynchronously(this, new ru.xtime_1_9R2.Task5(), 20L, 20L);
			return true;
		} else if (version.equals("v1_9_R1")) {
			Bukkit.getPluginManager().registerEvents(new ru.xtime_1_9R1.XListener(), this);
			Bukkit.getScheduler().runTaskTimerAsynchronously(this, new ru.xtime_1_9R1.Task5(), 20L, 20L);
			return true;
		} else if (version.equals("v1_8_R3")) {
			Bukkit.getPluginManager().registerEvents(new ru.xtime_1_8_R3.XListener(), this);
			Bukkit.getScheduler().runTaskTimerAsynchronously(this, new ru.xtime_1_8_R3.Task5(), 20L, 20L);
			return true;
		} else if (version.equals("v1_8_R2")) {
			Bukkit.getPluginManager().registerEvents(new ru.xtime_1_8_R2.XListener(), this);
			Bukkit.getScheduler().runTaskTimerAsynchronously(this, new ru.xtime_1_8_R2.Task5(), 20L, 20L);
			return true;
		} else if (version.equals("v1_8_R1")) {
			Bukkit.getPluginManager().registerEvents(new ru.xtime_1_8_R1.XListener(), this);
			Bukkit.getScheduler().runTaskTimerAsynchronously(this, new ru.xtime_1_8_R1.Task5(), 20L, 20L);
			return true;
		} else if (version.equals("v1_7_R4")) {
			Bukkit.getPluginManager().registerEvents(new ru.xtime_1_7_R4.XListener(), this);
			Bukkit.getScheduler().runTaskTimerAsynchronously(this, new ru.xtime_1_7_R4.Task5(), 20L, 20L);
			return true;
		}     
		return false;
	}
	
	@Override
	public void run() {
		if (!b){
			if (isUpdate()) {
				b = true;
				String message = ChatColor.translateAlternateColorCodes('&', "&b[ItemFixer] &cНайдено новое обновление! &7// &cNew update found");
				String message2 = ChatColor.translateAlternateColorCodes('&', "&b[ItemFixer] &chttp://rubukkit.org/threads/119485/");
				ConsoleCommandSender console = Bukkit.getConsoleSender();
				console.sendMessage(message);
				console.sendMessage(message2);
			}
		} else {
			String message = ChatColor.translateAlternateColorCodes('&', "&b[ItemFixer] &cНайдено новое обновление! &7// &cNew update found");
			String message2 = ChatColor.translateAlternateColorCodes('&', "&b[ItemFixer] &chttp://rubukkit.org/threads/119485/");
			ConsoleCommandSender console = Bukkit.getConsoleSender();
			console.sendMessage(message);
			console.sendMessage(message2);
		}
	}
	public boolean isUpdate() {
		try {
			String sourceLine = null;

			URL address;
			address = new URL("http://151.80.108.152/version.txt");

			InputStreamReader pageInput;
			pageInput = new InputStreamReader(address.openStream());
			BufferedReader source = new BufferedReader(pageInput);
			sourceLine = source.readLine();
			Integer int1 = Integer.valueOf(sourceLine);
			if (Bukkit.getPluginManager().getPlugin("ItemFixer").getDescription().getDescription() == null){
				String message = ChatColor.translateAlternateColorCodes('&', "&b[ItemFixer] &cНе удалось получить описание плагина // error. No description");
				String message2 = ChatColor.translateAlternateColorCodes('&', "&b[ItemFixer] &aНапишите об этой ошибке сюда: &crubukkit.org/threads/119485");
				ConsoleCommandSender console = Bukkit.getConsoleSender();
				console.sendMessage(message);
				console.sendMessage(message2);
				return false;
			}
			Integer version = Integer.valueOf(Bukkit.getPluginManager().getPlugin("ItemFixer").getDescription().getDescription().toString());
			if(!int1.equals(version)){
				return true;
			} else {
				return false;

			}
		}catch (Exception e) {
			String message = ChatColor.translateAlternateColorCodes('&', "&b[ItemFixer] &4Не удалось проверить обновление &e:( &7// &4Can't check update");
			String message2 = ChatColor.translateAlternateColorCodes('&', "&b[ItemFixer] &eНапишите ошибку ниже сюда: &crubukkit.org/threads/119485");
			ConsoleCommandSender console = Bukkit.getConsoleSender();
			console.sendMessage(message);
			console.sendMessage(message2);
			Bukkit.getLogger().log(Level.WARNING, "Ошибка:", e);
			return false;
		}
	}

}

