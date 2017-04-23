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
import ru.xtime.nbtfix.updater.PluginUpdater;
import ru.xtime.nbtfix.updater.UpdaterException;
import ru.xtime.nbtfix.updater.UpdaterResult;

public class Main extends JavaPlugin {

    private MagicAPI mapi;
    private ItemChecker checker;
    private ProtocolManager manager;
    private Logger logger;

    private final PluginUpdater updater = new PluginUpdater(this, "Dimatert9", "ItemFixer");

    @Override
    public void onEnable() {
        saveDefaultConfig();
        PluginManager pmanager = Bukkit.getPluginManager();
        String version = Bukkit.getServer().getClass().getPackage().getName().replace(".", ",").split(",")[3];
        logger = Bukkit.getLogger();
        mapi = getMagicAPI();
        checker = new ItemChecker(this);
        manager = ProtocolLibrary.getProtocolManager();
        manager.addPacketListener(new NBTListener(this, version));
        pmanager.registerEvents(new NBTBukkitListener(this), this);
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
        return (MagicAPI) magicPlugin;
    }

    public void checkUpdate() {
        try {
            UpdaterResult result = updater.checkUpdates();
            if (result.hasUpdates()) {
                getLogger().info("§aНовое обновление найдено! | The new version found!");
            } else {
                getLogger().info("§cОбновлений не найдено. | No updates found.");
            }
        } catch (UpdaterException e) {
            e.print();
        }
    }
}
