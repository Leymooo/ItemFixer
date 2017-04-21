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
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.java.JavaPlugin;

import com.comphenix.protocol.ProtocolLibrary;
import com.elmakers.mine.bukkit.api.magic.MagicAPI;

public class Main extends JavaPlugin {
    private Boolean hasUpdates = false;
    private MagicAPI mapi;
    ExploitCheck isExploit = new ExploitCheck(this);
    public void onEnable() {
        saveDefaultConfig();
        config();
        mapi = getMagicAPI();
        ProtocolLibrary.getProtocolManager().addPacketListener(new NBTHeldItemListener(this));
        ProtocolLibrary.getProtocolManager().addPacketListener(new NBTCreatListener(this));
        if (!this.getServer().getClass().getPackage().getName().replace(".",  ",").split(",")[3].startsWith("v1_11_R")) {
            ProtocolLibrary.getProtocolManager().addPacketListener(new PacketSpamFix(this));
        }
        Bukkit.getPluginManager().registerEvents(new NBTListener(this), this);
        Bukkit.getPluginManager().registerEvents(new TextureFix(), this);
        if (getConfig().getBoolean("check-update")) {
            new Thread(new Runnable() {
                @Override
                public void run() {
                    checkUpdate();
                    return;
                }
            }).start();
        }
        this.msgToCS("&aItemFixer enabled");
    }
    @Override
    public void onDisable() {
        ProtocolLibrary.getProtocolManager().removePacketListeners(this);
        Bukkit.getScheduler().cancelTasks(this);
        mapi = null;
        isExploit = null;
        NBTCreatListener.needCancel = null;
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
        if (!getConfig().isSet("check-update")) {
            getConfig().set("check-update", true);
        }
        isExploit.fillLists();
    }
    public boolean isExploit(ItemStack stack, String world) {
        return isExploit.isExploit(stack, world);
    }
    public boolean isMagicItem(ItemStack it) {
        return mapi != null && mapi.isWand(it);
    }
    public MagicAPI getMagicAPI() {
        Plugin magicPlugin = Bukkit.getPluginManager().getPlugin("Magic");
        if (magicPlugin == null || !magicPlugin.isEnabled() || !(magicPlugin instanceof MagicAPI)) {
            return null;
        }
        return (MagicAPI)magicPlugin;
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
