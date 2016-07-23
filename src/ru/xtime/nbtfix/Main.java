package ru.xtime.nbtfix;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;
import java.util.ArrayList;
import java.util.Map;
import java.util.logging.Level;

import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.plugin.java.JavaPlugin;

import com.comphenix.protocol.ProtocolLibrary;
import com.comphenix.protocol.wrappers.nbt.NbtCompound;
import com.comphenix.protocol.wrappers.nbt.NbtFactory;

public class Main extends JavaPlugin implements Runnable {
    Boolean hasUpdates;
    private ArrayList<String> nbt = new ArrayList<String>();
    private ArrayList<String> eggs = new ArrayList<String>();
    private ArrayList<String> armor = new ArrayList<String>();
    private ArrayList<String> book = new ArrayList<String>();
    private ArrayList<String> inventory = new ArrayList<String>();
    public void onEnable() {
        hasUpdates = false;
        this.saveDefaultConfig();
        nbt.addAll(this.getConfig().getStringList("nbt"));
        eggs.addAll(this.getConfig().getStringList("spawneggs"));
        armor.addAll(this.getConfig().getStringList("armorstand"));
        book.addAll(this.getConfig().getStringList("writenbook"));
        inventory.addAll(this.getConfig().getStringList("inventory"));
        ProtocolLibrary.getProtocolManager().addPacketListener(new NBTArmListener(this));
        ProtocolLibrary.getProtocolManager().addPacketListener(new NBTCreatListener(this));

        this.checkUpdate();
        this.getServer().getScheduler().runTaskTimerAsynchronously(this, this, 0, 18000L);

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

    public boolean isExploit(ItemStack stack, Player p) {
        boolean b = false;
        try {
            removeEnt(stack);
            Material mat = stack.getType();
            NbtCompound tag = (NbtCompound) NbtFactory.fromItemTag(stack);
            for (String a : nbt) {
                if (tag.containsKey(a)) {
                    tag.remove(a);
                    b = true;
                }
            }
            if (mat == Material.CHEST || mat == Material.TRAPPED_CHEST || mat == Material.DROPPER || mat == Material.DISPENSER) {
                for (String a : inventory) {
                    if (tag.containsKey(a)) {
                        tag.remove(a);
                        b = true;
                    }
                }

            } else if (mat == Material.ARMOR_STAND) {
                for (String a : armor) {
                    if (tag.containsKey(a)) {
                        tag.remove(a);
                        b = true;
                    }
                }
            } else if (mat == Material.WRITTEN_BOOK) {
                for (String a : book) {
                    if (tag.toString().contains(a)) {
                        p.getInventory().remove(stack);
                        b = true;  
                    }
                }
            } else if (mat == Material.MONSTER_EGG || mat == Material.MONSTER_EGGS) {
                for (String a : eggs) {
                    if (tag.toString().contains(a)) {
                        p.getInventory().remove(stack);
                        b = true;
                    }
                }
            }
        } catch (Exception e) {

        }
        return b;
    }
    @Override
    public void run() {
        checkUpdate();
    }
    private void checkUpdate() {
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
            this.getServer().getConsoleSender().sendMessage(ChatColor.translateAlternateColorCodes('&', "&b[ItemFixer] ".concat(line)));
        }
    }

}
