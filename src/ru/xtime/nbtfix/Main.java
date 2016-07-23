package ru.xtime.nbtfix;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;
import java.util.ArrayList;
import java.util.Map;
import java.util.logging.Level;

import org.bukkit.ChatColor;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.plugin.java.JavaPlugin;

import com.comphenix.protocol.ProtocolLibrary;
import com.comphenix.protocol.wrappers.nbt.NbtCompound;
import com.comphenix.protocol.wrappers.nbt.NbtFactory;

public class Main extends JavaPlugin implements Runnable {
    Boolean hasUpdates;
    @SuppressWarnings("serial")
    private ArrayList<String> nbt = new ArrayList<String>(){{
        add("Items");
        add("ActiveEffects");
        add("Command");
        add("powered");
        add("Equipment");
        add("Fuse");
        add("CustomName");
        add("AttributeModifiers");
        add("Unbreakable");
        add("ClickEvent");
        add("run_command");
        add("CustomPotionEffects");
    }};
    public void onEnable() {
        hasUpdates = false;
        this.getServer().getScheduler().runTaskTimerAsynchronously(this, this, 0, 18000L);
        ProtocolLibrary.getProtocolManager().addPacketListener(new NBTFixListener(this));
        this.msgToCS("&aItemFixer включен");
    }
   private void removeEnt(final ItemStack item) {
        if (item != null) {
            if (item.getEnchantments() != null) {
                for (final Map.Entry<Enchantment, Integer> ench : item.getEnchantments().entrySet()) {
                    final Enchantment Enchant = ench.getKey();
                    if (ench.getValue() > Enchant.getMaxLevel() || ench.getValue() < 0) {
                        final ItemMeta meta = item.getItemMeta();
                        meta.removeEnchant(Enchant);
                        item.setItemMeta(meta);
                        return;
                    }
                }
            }
        }
        return;
    }
   
    public boolean isExploit(ItemStack stack) {
        try {
            removeEnt(stack);
            NbtCompound tag = (NbtCompound) NbtFactory.fromItemTag(stack);
            if (isExploit(tag)) {
                return true;
            }
        } catch (Exception e) {
            // NBT read failed
        }
        return false;
    }

    private boolean isExploit(NbtCompound root) {
        boolean b = false;
        for (String a : nbt) {
            if (root.containsKey(a)) {
                root.remove(a);
                b = true;
            }
        }
        return b;
    }
    @Override
    public void run() {
        if (this.hasUpdates) {
            this.msgToCS(
                    "&cНайдено новое обновление! &7// &cNew update found",
                    "&chttp://rubukkit.org/threads/119485/"
            );
            return;
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
                return;
            }
            Integer currentBuild = Integer.valueOf(this.getDescription().getDescription());
            this.hasUpdates = !latestBuild.equals(currentBuild);
            return;
        } catch (IOException | NumberFormatException e) {
            this.msgToCS(
                    "&4Не удалось проверить обновление &e:( &7// &4Can't check update",
                    "&eНапишите ошибку ниже сюда: &chttp://rubukkit.org/threads/119485"
            );
            this.getLogger().log(Level.WARNING, "Ошибка: ", e);
        }

        return;
    }

    private void msgToCS(String... message) {
        for (String line : message) {
            this.getServer().getConsoleSender().sendMessage(ChatColor.translateAlternateColorCodes('&', "&b[ItemFixer] ".concat(line))
            );
        }
    }

}
