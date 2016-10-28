package ru.xtime.nbtfix;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;
import java.util.ArrayList;
import java.util.logging.Level;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.java.JavaPlugin;

import com.comphenix.protocol.ProtocolLibrary;

public class Main extends JavaPlugin implements Runnable {
    private Boolean hasUpdates;
    Boolean mc19;
    String ignoretag;
    ExploitCheck isExploit = new ExploitCheck(this);
    public void onEnable() {
        hasUpdates = false;
        isExploit.mc17 = this.getServer().getClass().getPackage().getName().replace(".",  ",").split(",")[3].startsWith("v1_7_R");
        mc19 = (this.getServer().getClass().getPackage().getName().replace(".",  ",").split(",")[3].startsWith("v1_9_R") || this.getServer().getClass().getPackage().getName().replace(".",  ",").split(",")[3].startsWith("v1_10_R"));
        saveDefaultConfig();
        config();
        ProtocolLibrary.getProtocolManager().addPacketListener(new NBTHeldItemListener(this));
        ProtocolLibrary.getProtocolManager().addPacketListener(new NBTCreatListener(this));
        Bukkit.getPluginManager().registerEvents(new NBTListener(this), this);
        if (getConfig().getBoolean("Timer.Enabled")) {
            this.getServer().getScheduler().runTaskTimerAsynchronously(this, new Timer(this), 10L , (getConfig().getInt("Timer.delay")*20));
        }
        this.getServer().getScheduler().runTaskTimerAsynchronously(this, this, 0, 18000L);
        this.getCommand("itemfixer").setExecutor(new IgnoreCmd(this));
        this.msgToCS("&aItemFixer включен");
    }
    public void config() {
        reloadConfig();
        if (!getConfig().isSet("remove-invalid-enchants")) getConfig().set("remove-invalid-enchants", false);
        if (!getConfig().isSet("check-enchants")) getConfig().set("check-enchants", true);
        if (!getConfig().isSet("nbt")) {
            ArrayList<String> list = new ArrayList<String>();
            list.add("ActiveEffects");
            list.add("Command");
            list.add("CustomName");
            list.add("AttributeModifiers");
            list.add("Unbreakable");
            list.add("CustomPotionEffects");
            getConfig().set("nbt", list);
        }
        if (!getConfig().isSet("ignore-worlds")) {
            ArrayList<String> list = new ArrayList<String>();
            list.add("random_world228");
            getConfig().set("ignore-worlds", list);
        }
        if (!getConfig().isSet("spawneggs")) {
            ArrayList<String> list = new ArrayList<String>();
            list.add("Fuse");
            list.add("CustomNames");
            list.add("powered");
            list.add("ActiveEffects");
            list.add("ExplosionPower");
            list.add("Size");
            getConfig().set("spawneggs", list);
        }
        if (!getConfig().isSet("Timer")) {
            getConfig().set("Timer.delay", 120);
            getConfig().set("Timer.CheckInventory", false);
            getConfig().set("Timer.CheckArmor", true);
            getConfig().set("Timer.Enabled", true);
        }
        if (!getConfig().isSet("armorstand")) {
            ArrayList<String> list = new ArrayList<String>();
            list.add("EntityTag");
            getConfig().set("armorstand", list);
        }
        if (!getConfig().isSet("writtenbook")) {
            ArrayList<String> list = new ArrayList<String>();
            list.add("ClickEvent");
            list.add("run_command");
            getConfig().set("writtenbook", list);
        }
        if (!getConfig().isSet("inventory")) {
            ArrayList<String> list = new ArrayList<String>();
            list.add("BlockEntityTag");
            getConfig().set("inventory", list);
        }
        if (!getConfig().isSet("ignoreTag")) {
            getConfig().set("ignoreTag", "unknown"+System.currentTimeMillis());
        }
        saveConfig();
        saveDefaultConfig();
        reloadConfig();
        ignoretag = getConfig().getString("ignoreTag");
        isExploit.fillLists();
    }
    public boolean isExploit(ItemStack stack, String world) {
        return isExploit.isExploit(stack, world);
    }
    @Override
    public void run() {
        if (!this.hasUpdates) {
            if (this.getDescription().getDescription() == null) {
                this.msgToCS(
                        "&cНе удалось получить описание плагина // error. No description",
                        "&aНапишите об этой ошибке сюда: &chttp://rubukkit.org/threads/119485"
                        );
                return;
            }
            try {
                URL address = new URL("http://151.80.108.152/version.txt");
                InputStreamReader pageInput = new InputStreamReader(address.openStream());
                BufferedReader source = new BufferedReader(pageInput);
                Integer latestBuild = Integer.valueOf(source.readLine());
                Integer currentBuild = Integer.valueOf(this.getDescription().getDescription());
                this.hasUpdates = !latestBuild.equals(currentBuild);
            } catch (IOException | NumberFormatException e) {
                this.msgToCS(
                        "&4Не удалось проверить обновление &e:( &7// &4Can't check update",
                        "&eНапишите ошибку ниже сюда: &chttp://rubukkit.org/threads/119485"
                        );
                this.getLogger().log(Level.WARNING, "Ошибка: ", e);
            }
        }
        if (this.hasUpdates) {
            this.msgToCS(
                    "&cНайдено новое обновление! &7// &cNew update found",
                    "&chttp://rubukkit.org/threads/119485/"
                    );
        }
        return;
    }

    private void msgToCS(String... message) {
        for (String line : message) {
            this.getServer().getConsoleSender().sendMessage(ChatColor.translateAlternateColorCodes('&', "&b[ItemFixer] ".concat(line)));
        }
    }

}
