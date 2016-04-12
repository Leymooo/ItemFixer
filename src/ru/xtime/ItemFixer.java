package ru.xtime;

import java.io.File;
import java.io.FileOutputStream;
import java.net.URL;
import java.net.URLClassLoader;
import java.nio.channels.Channels;
import java.nio.channels.ReadableByteChannel;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.event.Listener;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.java.JavaPlugin;

public class ItemFixer extends JavaPlugin {
    static ItemFixer plugin;

	public void onEnable() {
		plugin = this;
		if (setupItemFixer()) {
			Bukkit.getScheduler().runTaskTimerAsynchronously((Plugin)this, (Runnable)new UpdateChecker(), 6000L, 6000L);
		    String message = ChatColor.translateAlternateColorCodes('&', "&b[ItemFixer] &aПлагин включен! &7// &aPlugin enabled");
		    ConsoleCommandSender console = Bukkit.getServer().getConsoleSender();
		    console.sendMessage(message);
		} else {
		    String message = ChatColor.translateAlternateColorCodes('&', "&b[ItemFixer] &aВерсия не поддерживается. &7// &aServer version is not supported");
		    ConsoleCommandSender console = Bukkit.getServer().getConsoleSender();
		    console.sendMessage(message);
			Bukkit.getPluginManager().disablePlugin(this);
		}
		if (UpdateChecker.isUpdate()) {
		    String message = ChatColor.translateAlternateColorCodes('&', "&b[ItemFixer] &aЕсть обновление. &chttp:rubukkit.org/threads/119485/ &7// &aNew update found");
		    String message2 = ChatColor.translateAlternateColorCodes('&', "&b[ItemFixer] &eПытаюсь поставить обновление &7// &e Try to update the plugin");
		    ConsoleCommandSender console = Bukkit.getServer().getConsoleSender();
		    console.sendMessage(message);
		    console.sendMessage(message2);
		    Update();
		} else {
		    String message = ChatColor.translateAlternateColorCodes('&', "&b[ItemFixer] &aОбновление не найдено! &7// &aUpdate not found");
		    ConsoleCommandSender console = Bukkit.getServer().getConsoleSender();
		    console.sendMessage(message);
		}
	}
	private boolean setupItemFixer() {
		String version;
		version = Bukkit.getServer().getClass().getPackage().getName().replace(".",  ",").split(",")[3];
	    String message = ChatColor.translateAlternateColorCodes('&', "&aServer version &c" + version);
	    ConsoleCommandSender console = Bukkit.getServer().getConsoleSender();
	    console.sendMessage(message);
		if (version.equals("v1_9_R1")) {
			this.getServer().getPluginManager().registerEvents((Listener)new ru.xtime_1_9R1.XListener(), (Plugin)this);
			Bukkit.getScheduler().runTaskTimerAsynchronously((Plugin)this, (Runnable)new ru.xtime_1_9R1.Task5(), 20L, 20L);
			return true;
		} else if (version.equals("v1_8_R3")) {
			this.getServer().getPluginManager().registerEvents((Listener)new ru.xtime_1_8_R3.XListener(), (Plugin)this);
			Bukkit.getScheduler().runTaskTimerAsynchronously((Plugin)this, (Runnable)new ru.xtime_1_8_R3.Task5(), 20L, 20L);
			return true;
		}  
		return false;
	}
    @SuppressWarnings("deprecation")
	public boolean Update(){
        try {
        	for (Thread thread : Thread.getAllStackTraces().keySet()) {
    			if (thread.getClass().getClassLoader() == plugin.getClass().getClassLoader()) {
    				thread.interrupt();
    				thread.join(2000);
    				if (thread.isAlive()) {
    					thread.stop();
    				}
    			}
    		}
    		Plugin p = Bukkit.getPluginManager().getPlugin("ItemFixer");
            Utils.unload(p);
            ((URLClassLoader)plugin.getClass().getClassLoader()).close();
            System.gc();
            System.gc();
            System.gc();
            String url="https://www.dropbox.com/s/ztxjbwn4vzdyiyb/ItemFixer.jar?dl=1";
            String filename=this.getFile().getName().toString();
            URL download=new URL(url);
            ReadableByteChannel rbc=Channels.newChannel(download.openStream());
            FileOutputStream fileOut = new FileOutputStream("plugins/"+filename);
            fileOut.getChannel().transferFrom(rbc, 0, 1 << 24);
            fileOut.flush();
            fileOut.close();
            rbc.close();
            Bukkit.getPluginManager().loadPlugin(new File("plugins/"+filename));
            p = Bukkit.getPluginManager().getPlugin("ItemFixer");
            Bukkit.getPluginManager().enablePlugin(p);
    		return true;
        }catch(Exception e){
        	return false;
        }
    }
}

