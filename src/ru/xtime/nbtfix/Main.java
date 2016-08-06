package ru.xtime.nbtfix;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;
import java.util.ArrayList;
import java.util.Map;
import java.util.logging.Level;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.plugin.java.JavaPlugin;

import com.comphenix.protocol.ProtocolLibrary;
import com.comphenix.protocol.wrappers.nbt.NbtBase;
import com.comphenix.protocol.wrappers.nbt.NbtCompound;
import com.comphenix.protocol.wrappers.nbt.NbtFactory;
import com.comphenix.protocol.wrappers.nbt.NbtList;

public class Main extends JavaPlugin implements Runnable {
    private Boolean hasUpdates;
    private Boolean mc17;
    Boolean mc19;
    private Boolean skullfix;
    private Boolean removeInvalidEnch;
    private ArrayList<String> nbt = new ArrayList<String>();
    private ArrayList<String> eggs = new ArrayList<String>();
    private ArrayList<String> armor = new ArrayList<String>();
    private ArrayList<String> book = new ArrayList<String>();
    private ArrayList<String> inventory = new ArrayList<String>();
    public void onEnable() {
        hasUpdates = false;
        mc17 = this.getServer().getClass().getPackage().getName().replace(".",  ",").split(",")[3].startsWith("v1_7_R");
        mc19 = (this.getServer().getClass().getPackage().getName().replace(".",  ",").split(",")[3].startsWith("v1_9_R") || this.getServer().getClass().getPackage().getName().replace(".",  ",").split(",")[3].startsWith("v1_10_R"));
        config();
        ProtocolLibrary.getProtocolManager().addPacketListener(new NBTHeldItemListener(this));
        ProtocolLibrary.getProtocolManager().addPacketListener(new NBTCreatListener(this));
        Bukkit.getPluginManager().registerEvents(new NBTInteractListener(this), this);
        this.getServer().getScheduler().runTaskTimerAsynchronously(this, this, 0, 18000L);
        this.msgToCS("&aItemFixer включен");
    }
    private void config() {
        saveDefaultConfig();
        if (!getConfig().isSet("remove-invalid-enchants")) getConfig().set("remove-invalid-enchants", false);
        if (!getConfig().isSet("fix-skull-exploit")) getConfig().set("fix-skull-exploit", true);
        if (!getConfig().isSet("nbt")) {
            ArrayList<String> list = new ArrayList<String>();
            list.add("Items");
            list.add("ActiveEffects");
            list.add("Command");
            list.add("CustomName");
            list.add("AttributeModifiers");
            list.add("Unbreakable");
            list.add("CustomPotionEffects");
            getConfig().set("nbt", list);
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
        saveConfig();
        saveDefaultConfig();
        reloadConfig();
        removeInvalidEnch = this.getConfig().getBoolean("remove-invalid-enchants");
        skullfix = this.getConfig().getBoolean("fix-skull-exploit");
        nbt.addAll(this.getConfig().getStringList("nbt"));
        eggs.addAll(this.getConfig().getStringList("spawneggs"));
        if (!eggs.contains("Size")) eggs.add("Size");
        armor.addAll(this.getConfig().getStringList("armorstand"));
        book.addAll(this.getConfig().getStringList("writtenbook"));
        inventory.addAll(this.getConfig().getStringList("inventory"));
    }
    private void removeEnt(ItemStack item) {
        if (item == null) return;
        if (item.getType() == Material.AIR) return;
        if (item.getEnchantments() == null) return;
        for (final Map.Entry<Enchantment, Integer> ench : item.getEnchantments().entrySet()) {
            final ItemMeta meta = item.getItemMeta();
            final Enchantment Enchant = ench.getKey();
            if (!Enchant.canEnchantItem(item) && removeInvalidEnch) meta.removeEnchant(Enchant);
            if (ench.getValue() > Enchant.getMaxLevel() || ench.getValue() < 0) meta.removeEnchant(Enchant);
            item.setItemMeta(meta);
        }
        return;
    }
    @SuppressWarnings("rawtypes")
    private boolean isExploitSkull(NbtCompound tag) {
        if (tag.containsKey("SkullOwner")) {
            NbtCompound skullOwner = tag.getCompound("SkullOwner");
            if (skullOwner.containsKey("Properties")) {
                NbtCompound properties = skullOwner.getCompound("Properties");
                if (properties.containsKey("textures")) {
                    NbtList<NbtBase> textures = properties.getList("textures");
                    for (NbtBase texture : textures.asCollection()) {
                        if (texture instanceof NbtCompound) {
                            if (!((NbtCompound) texture).containsKey("Signature")) {
                                if (((NbtCompound) texture).containsKey("Value")) {
                                    if (((NbtCompound) texture).getString("Value").trim().length() > 0) {
                                        return false;
                                    }
                                }
                                tag.remove("SkullOwner");
                                return true;
                            }
                        }
                    }
                }
            }
        }

        return false;
    }
    public boolean isExploit(ItemStack stack, Player p) {
        boolean b = false;
        try {
            removeEnt(stack);
            Material mat = stack.getType();
            NbtCompound tag = (NbtCompound) NbtFactory.fromItemTag(stack);
            // Фиксим CrashChest. CrashItem // Фиксим возможную утечку.
            if (mat == Material.CHEST || mat == Material.NAME_TAG) {
                if (tag.toString().length() > 1000) {
                    tag.getKeys().clear();
                    b = true;
                }
            }
            //
            for (String a : nbt) {
                if (tag.containsKey(a)) {
                    tag.remove(a);
                    b = true;
                }
            }
            if (mat == Material.CHEST || mat == Material.TRAPPED_CHEST || mat == Material.DROPPER || mat == Material.DISPENSER || mat == Material.COMMAND || mat == Material.COMMAND_MINECART || mat == Material.HOPPER || mat == Material.HOPPER_MINECART) {
                for (String a : inventory) {
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
                        tag.getKeys().clear();
                        b = true;
                    }
                }
            } else if (!mc17 && mat == Material.ARMOR_STAND) {
                for (String a : armor) {
                    if (tag.containsKey(a)) {
                        tag.remove(a);
                        b = true;
                    }
                }
            } else if ((mat == Material.SKULL || mat == Material.SKULL_ITEM) && skullfix && stack.getDurability() == 3) {
                if (isExploitSkull(tag)) b = true;
            }
        } catch (Exception e) {
            return b;
        }
        return b;
    }
    @Override
    public void run() {
        checkUpdate();
    }
    private void checkUpdate() {
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
