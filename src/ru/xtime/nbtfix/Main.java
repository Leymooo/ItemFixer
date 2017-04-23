package ru.xtime.nbtfix;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.bukkit.Bukkit;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;

import com.comphenix.protocol.ProtocolLibrary;
import com.comphenix.protocol.ProtocolManager;
import com.elmakers.mine.bukkit.api.magic.MagicAPI;

public class Main extends JavaPlugin {
    private MagicAPI mapi;
    private ItemChecker checker;
    private ProtocolManager manager;
    private Logger logger;
    
    @Override
    public void onEnable() {
        saveDefaultConfig();
        PluginManager pmanager = Bukkit.getPluginManager();
        String version = Bukkit.getServer().getClass().getPackage().getName().replace(".",  ",").split(",")[3];
        logger = Bukkit.getLogger();
        mapi = getMagicAPI();
        checker = new ItemChecker(this);
        manager = ProtocolLibrary.getProtocolManager();
        manager.addPacketListener(new NBTHeldItemListener(this));
        manager.addPacketListener(new NBTCreatListener(this));
        if (!version.startsWith("v1_11_R")) {
            manager.addPacketListener(new PacketSpamFix(this));
        }
        pmanager.registerEvents(new NBTListener(this), this);
        pmanager.registerEvents(new TextureFix(version), this);
        if (getConfig().getBoolean("check-update")) checkUpdate();
        logger.info("ItemFixer enabled");
    }
    
    @Override
    public void onDisable() {
        manager.removePacketListeners(this);
        mapi = null;
        checker = null;
        logger = null;
        manager = null;
        NBTCreatListener.cancel = null;
    }
    public boolean checkItem(ItemStack stack, String world) {
        return checker.isExploit(stack, world);
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
         new Thread(()-> {
            try {
                URL address = new URL("http://151.80.108.152/version.txt");
                InputStreamReader pageInput = new InputStreamReader(address.openStream());
                BufferedReader source = new BufferedReader(pageInput);
                Integer latestBuild = Integer.valueOf(source.readLine());
                Integer currentBuild = Integer.valueOf(this.getDescription().getDescription());
                if (latestBuild!=currentBuild) logger.warning("Найдено новое обновление! // New update found");
            } catch (IOException | NumberFormatException e) {
                logger.warning("Не удалось проверить обновление :( // Can't check update");
                logger.log(Level.WARNING, "Error: ", e);
            }
        }).start();
    }
}
