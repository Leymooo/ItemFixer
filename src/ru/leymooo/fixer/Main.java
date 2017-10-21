package ru.leymooo.fixer;

import java.io.File;

import me.Fupery.ArtMap.Recipe.ArtMaterial;
import me.catcoder.updatechecker.PluginUpdater;
import me.catcoder.updatechecker.UpdaterException;
import me.catcoder.updatechecker.UpdaterResult;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.HandlerList;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;

import com.comphenix.protocol.ProtocolLibrary;
import com.comphenix.protocol.ProtocolManager;
import com.elmakers.mine.bukkit.api.magic.MagicAPI;

public class Main extends JavaPlugin {

    private boolean useArtMap;
    private MagicAPI mapi;
    private ItemChecker checker;
    private ProtocolManager manager;
    public String version;
    private final PluginUpdater updater = new PluginUpdater(this, "Dimatert9", "ItemFixer");

    @Override
    public void onEnable() {
        saveDefaultConfig();
        checkNewConfig();
        PluginManager pmanager = Bukkit.getPluginManager();
        version = Bukkit.getServer().getClass().getPackage().getName().split("\\.")[3];
        mapi = getMagicAPI();
        useArtMap = initArtMapApi();
        checker = new ItemChecker(this);
        manager = ProtocolLibrary.getProtocolManager();
        manager.addPacketListener(new NBTListener(this, version));
        pmanager.registerEvents(new NBTBukkitListener(this), this);
        pmanager.registerEvents(new TextureFix(version, this), this);
        if (getConfig().getBoolean("check-update")) checkUpdate();
        Bukkit.getConsoleSender().sendMessage("§b[ItemFixer] §aenabled");
    }

    @Override
    public void onDisable() {
        HandlerList.unregisterAll(this);
        manager.removePacketListeners(this);
        NBTListener.cancel.invalidateAll();
        NBTListener.cancel = null;
        mapi = null;
        checker = null;
        manager = null;
    }

    public boolean checkItem(ItemStack stack, Player p) {
        return checker.isHackedItem(stack, p);
    }

    public boolean isMagicItem(ItemStack it) {
        return mapi != null && mapi.isWand(it);
    }

    private void checkNewConfig() {
        if (!getConfig().isSet("ignored-tags")) {
            File config = new File(getDataFolder(),"config.yml");
            config.delete();
            saveDefaultConfig();
        }
        if (getConfig().isSet("max-pps")) {
            getConfig().set("max-pps", null);
            getConfig().set("max-pps-kick-msg", null);
            saveConfig();
        }
    }   

    private MagicAPI getMagicAPI() {
        Plugin magicPlugin = Bukkit.getPluginManager().getPlugin("Magic");
        if (magicPlugin == null || !magicPlugin.isEnabled() || !(magicPlugin instanceof MagicAPI)) {
            return null;
        }
        return (MagicAPI) magicPlugin;
    }

    public boolean isUnsupportedVersion() {
        return version.startsWith("v1_11_R") || version.startsWith("v1_12_R") || version.startsWith("v1_13_R");
    }

    private boolean initArtMapApi() {
        Plugin plugin = Bukkit.getPluginManager().getPlugin("ArtMap");
        return plugin != null && plugin.isEnabled();
    }

    public boolean isArtMapItem(ItemStack stack) {
        if (useArtMap) {
            for (ArtMaterial art : ArtMaterial.values()) {
                if (art.isValidMaterial(stack)) {
                    return true;
                }
            }
        }
        return false;
    }
    private void checkUpdate() {
        new Thread(()-> {
            try {
                UpdaterResult result = updater.checkUpdates();
                if (result.hasUpdates()) {
                    Bukkit.getConsoleSender().sendMessage("§b[ItemFixer] §cНовое обновление найдено! | The new version found!");
                } else {
                    Bukkit.getConsoleSender().sendMessage("§b[ItemFixer] §aОбновлений не найдено. | No updates found.");
                }
            } catch (UpdaterException e) {
                e.print();
            }
        }).start();
    }
}
