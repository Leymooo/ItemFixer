package ru.xtime.nbtfix;

import java.util.ArrayList;
import java.util.Map;

import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.plugin.java.JavaPlugin;

import com.comphenix.protocol.ProtocolLibrary;
import com.comphenix.protocol.wrappers.nbt.NbtCompound;
import com.comphenix.protocol.wrappers.nbt.NbtFactory;

public class Main extends JavaPlugin {
    @SuppressWarnings("serial")
    public ArrayList<String> nbt = new ArrayList<String>(){{
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
        ProtocolLibrary.getProtocolManager().addPacketListener(new NBTFixListener(this));
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

    public boolean isExploit(NbtCompound root) {
        boolean b = false;
        for (String a : nbt) {
            if (root.containsKey(a)) {
                root.remove(a);
                b = true;
            }
        }
        return b;
    }


}
