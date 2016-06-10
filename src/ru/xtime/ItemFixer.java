package ru.xtime;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStreamReader;
import java.net.URL;
import java.net.URLClassLoader;
import java.nio.channels.Channels;
import java.nio.channels.ReadableByteChannel;
import java.util.logging.Level;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.java.JavaPlugin;

public class ItemFixer extends JavaPlugin implements Runnable {
	Boolean b;
	public void onEnable() {
		b = false;
		if (setupItemFixer()) {
			Bukkit.getScheduler().runTaskTimerAsynchronously(this, this, 6000L, 6000L);
			Bukkit.getConsoleSender().sendMessage(ChatColor.translateAlternateColorCodes('&', "&b[ItemFixer] &aПлагин включен! &7// &aPlugin enabled"));
		} else {
			Bukkit.getConsoleSender().sendMessage(ChatColor.translateAlternateColorCodes('&', "&b[ItemFixer] &aВерсия не поддерживается. &7// &aServer version is not supported"));
			Bukkit.getPluginManager().disablePlugin(this);
		}
		if (isUpdate()) {
			Bukkit.getConsoleSender().sendMessage(ChatColor.translateAlternateColorCodes('&', "&b[ItemFixer] &aЕсть обновление. &chttp:rubukkit.org/threads/119485/ &7// &aNew update found"));
			Bukkit.getConsoleSender().sendMessage(ChatColor.translateAlternateColorCodes('&', "&b[ItemFixer] &eПытаюсь поставить обновление &7// &e Try to update the plugin"));
			Update();
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
	@SuppressWarnings("deprecation")
	public void Update(){
		try {
			for (Thread thread : Thread.getAllStackTraces().keySet()) {
				if (thread.getClass().getClassLoader() == this.getClass().getClassLoader()) {
					thread.interrupt();
					thread.join(2000);
					if (thread.isAlive()) {
						thread.stop();
					}
				}
			}
			Plugin p = Bukkit.getPluginManager().getPlugin("ItemFixer");
			Utils.unload(p);
			((URLClassLoader)this.getClass().getClassLoader()).close();
			System.gc();
			String url="https://www.dropbox.com/s/ztxjbwn4vzdyiyb/ItemFixer.jar?dl=1";
			String filename = getFile().getName().toString();
			URL download = new URL(url);
			ReadableByteChannel rbc = Channels.newChannel(download.openStream());
			FileOutputStream fileOut = new FileOutputStream("plugins/"+filename);
			fileOut.getChannel().transferFrom(rbc, 0, 1 << 24);
			fileOut.flush();
			fileOut.close();
			rbc.close();
			Bukkit.getPluginManager().loadPlugin(new File("plugins/"+filename));
			p = Bukkit.getPluginManager().getPlugin("ItemFixer");
			Bukkit.getPluginManager().enablePlugin(p);
			return;
		}catch(Exception e){
			Bukkit.getLogger().log(Level.WARNING, "Ошибка при обновлении плагина! Обновите плагин вручную! http:rubukkit.org/threads/119485/", e);
			return;
		}
	}
	@Override
	public void run() {
		if (!b){
			if (isUpdate()) {
				b = true;
				String message = ChatColor.translateAlternateColorCodes('&', "&b[ItemFixer] &cНайдено новое обновление! &7// &cNew update found");
				String message2 = ChatColor.translateAlternateColorCodes('&', "&b[ItemFixer] &chttp://rubukkit.org/threads/119485/");
				String message3 = ChatColor.translateAlternateColorCodes('&', "&b[ItemFixer] &eПытаюсь поставить обновление &7// &e Try to update the plugin");
				ConsoleCommandSender console = Bukkit.getConsoleSender();
				console.sendMessage(message);
				console.sendMessage(message2);
				console.sendMessage(message3);
				Update();
			}
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
			if(int1 > version){
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

