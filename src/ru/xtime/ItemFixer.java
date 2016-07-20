package ru.xtime;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;
import java.util.logging.Level;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.plugin.java.JavaPlugin;

public class ItemFixer extends JavaPlugin implements Runnable {

    private boolean hasUpdates = false;

    @Override
    public void onEnable() {
        if (!setupItemFixer()) {
            this.msgToCS("&aВерсия сервера не поддерживается. &7// &aServer version is not supported");
            this.getServer().getPluginManager().disablePlugin(this);
            return;
        }

        this.getServer().getScheduler().runTaskTimerAsynchronously(this, this, 18000L, 18000L);
        this.checkForUpdate();
        this.msgToCS("&aПлагин включен! &7// &aPlugin enabled");
    }

    private boolean setupItemFixer() {
		Reflection.nbt.add("Items");
		Reflection.nbt.add("ActiveEffects");
		Reflection.nbt.add("Command");
		Reflection.nbt.add("powered");
		Reflection.nbt.add("Equipment");
		Reflection.nbt.add("Fuse");
		Reflection.nbt.add("CustomName");
		Reflection.nbt.add("AttributeModifiers");
		Reflection.nbt.add("Unbreakable");
		Reflection.nbt.add("ClickEvent");
		Reflection.nbt.add("run_command");
		Reflection.nbt.add("CustomPotionEffects");
        String version = this.getServer().getClass().getPackage().getName().replace(".", ",").split(",")[3];
        this.msgToCS("&aServer version &c" + version);
        if (version.equals("v1_10_R1") || version.equals("v1_9_R1") || version.equals("v1_9_R2")) {
            Bukkit.getScheduler().runTaskTimerAsynchronously(this, new Task19(), 20L, 20L);
    		return true;
        } else if (version.equals("v1_8_R1") || version.equals("v1_8_R2") || version.equals("v1_8_R3") || version.equals("v1_7_R4")) {
            Bukkit.getScheduler().runTaskTimerAsynchronously(this, new Task17(), 20L, 20L);
    		return true;
        }
		return false;
    }

    @Override
    public void run() {
        this.checkForUpdate();
    }

    private boolean checkForUpdate() {
        if (this.hasUpdates) {
            this.msgToCS(
                    "&cНайдено новое обновление! &7// &cNew update found",
                    "&chttp://rubukkit.org/threads/119485/"
            );
            return true;
        }

        try {
            URL address = new URL("http://151.80.108.152/version.txt");
            InputStreamReader pageInput = new InputStreamReader(address.openStream());
            BufferedReader source = new BufferedReader(pageInput);
            Integer latestBuild = Integer.valueOf(source.readLine());
            if (this.getDescription().getDescription() == null) {
                this.msgToCS(
                        "&cНе удалось получить описание плагина // error. No description",
                        "&aНапишите об этой ошибке сюда: &chttp://rubukkit.org/threads/119485"
                );
                return false;
            }
            Integer currentBuild = Integer.valueOf(this.getDescription().getDescription());
            this.hasUpdates = !latestBuild.equals(currentBuild);
            return this.hasUpdates;
        } catch (IOException | NumberFormatException e) {
            this.msgToCS(
                    "&4Не удалось проверить обновление &e:( &7// &4Can't check update",
                    "&eНапишите ошибку ниже сюда: &chttp://rubukkit.org/threads/119485"
            );
            this.getLogger().log(Level.WARNING, "Ошибка: ", e);
        }

        return false;
    }

    //Message to ConsoleSender
    private void msgToCS(String... message) {
        for (String line : message) {
            this.getServer().getConsoleSender().sendMessage(ChatColor.translateAlternateColorCodes('&', "&b[ItemFixer] ".concat(line))
            );
        }
    }
}