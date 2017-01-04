package ru.xtime.nbtfix;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;
import java.util.logging.Level;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.java.JavaPlugin;

import com.comphenix.protocol.ProtocolLibrary;

public class Main extends JavaPlugin {
    private Boolean hasUpdates;
    ExploitCheck isExploit = new ExploitCheck(this);
    public void onEnable() {
        hasUpdates = false;
        saveDefaultConfig();
        config();
        ProtocolLibrary.getProtocolManager().addPacketListener(new NBTHeldItemListener(this));
        ProtocolLibrary.getProtocolManager().addPacketListener(new NBTCreatListener(this));
        Bukkit.getPluginManager().registerEvents(new NBTListener(this), this);
        new Thread(new Runnable() {
            @Override
            public void run() {
                checkUpdate();
                return;
            }
        }).start();
        this.msgToCS("&aItemFixer enabled");
    }
    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (sender instanceof ConsoleCommandSender) {
            config();
            sender.sendMessage("Config reloaded");
        }
        return true;
    }
    public void config() {
        reloadConfig();
        saveConfig();
        isExploit.fillLists();
    }
    public boolean isExploit(ItemStack stack, String world) {
        return isExploit.isExploit(stack, world);
    }
    public void checkUpdate() {
        try {
            URL address = new URL("http://151.80.108.152/version.txt");
            InputStreamReader pageInput = new InputStreamReader(address.openStream());
            BufferedReader source = new BufferedReader(pageInput);
            Integer latestBuild = Integer.valueOf(source.readLine());
            Integer currentBuild = Integer.valueOf(this.getDescription().getDescription());
            this.hasUpdates = !latestBuild.equals(currentBuild);
        } catch (IOException | NumberFormatException e) {
            this.msgToCS(
                    "&4Не удалось проверить обновление &e:( &7// &4Can't check update"
                    );
            this.getLogger().log(Level.WARNING, "Ошибка: ", e);
        }
        if (this.hasUpdates) {
            this.msgToCS(
                    "&cНайдено новое обновление! &7// &cNew update found"
                    );
        }
    }
    private void msgToCS(String... message) {
        for (String line : message) {
            this.getServer().getConsoleSender().sendMessage(ChatColor.translateAlternateColorCodes('&', "&b[ItemFixer] ".concat(line)));
        }
    }

}
