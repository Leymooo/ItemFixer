package ru.xtime;

import java.util.ArrayList;
import java.util.Map;

import org.bukkit.Bukkit;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

public class Reflection
{
    public static ArrayList<String> nbt = new ArrayList<String>();
    private static ItemStack bukkitItem;
    
    public static void removeNbt(ItemStack item) {
        removeEnt(item);
        bukkitItem = item.clone();
        for (int i=0;i<nbt.size();i++) {
            final ItemStack is = remove(bukkitItem, nbt.get(i));
            if (is != null && is.getItemMeta() != null) {
                item.setItemMeta(is.getItemMeta());
                bukkitItem = is.clone();
            }
        }
    }
    private static boolean removeEnt(final ItemStack item) {
        if (item != null) {
            if (item.getEnchantments() != null) {
                for (final Map.Entry<Enchantment, Integer> ench : item.getEnchantments().entrySet()) {
                    final Enchantment Enchant = ench.getKey();
                    if (ench.getValue() > Enchant.getMaxLevel() || ench.getValue() < 0) {
                        final ItemMeta meta = item.getItemMeta();
                        meta.removeEnchant(Enchant);
                        item.setItemMeta(meta);
                        return true;
                    }
                }
            }
        }
        return false;
    }
    @SuppressWarnings("rawtypes")
    private static Class getCraftItemStack() {
        String version = Bukkit.getServer().getClass().getPackage().getName().replace(".", ",").split(",")[3];
        try {
            Class c = Class.forName("org.bukkit.craftbukkit." + version + ".inventory.CraftItemStack");
            return c;
        } catch (Exception ex) {
            ex.printStackTrace();
            return null;
        }
    }


    private static Object setNBTTag(Object NBTTag, Object NMSItem) {
        try {
            java.lang.reflect.Method method;
            method = NMSItem.getClass().getMethod("setTag", NBTTag.getClass());
            method.invoke(NMSItem, NBTTag);
            return NMSItem;
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return null;
    }

    @SuppressWarnings("unchecked")
    private static Object getNMSItemStack(ItemStack item) {
        @SuppressWarnings("rawtypes")
        Class cis = getCraftItemStack();
        java.lang.reflect.Method method;
        try {
            method = cis.getMethod("asNMSCopy", ItemStack.class);
            Object answer = method.invoke(cis, item);
            return answer;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    @SuppressWarnings({"unchecked"})
    private static ItemStack getBukkitItemStack(Object item) {
        @SuppressWarnings("rawtypes")
        Class cis = getCraftItemStack();
        java.lang.reflect.Method method;
        try {
            method = cis.getMethod("asCraftMirror", item.getClass());
            Object answer = method.invoke(cis, item);
            return (ItemStack) answer;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    @SuppressWarnings({"unchecked"})
    private static Object getNBTTagCompound(Object nmsitem) {
        @SuppressWarnings("rawtypes")
        Class c = nmsitem.getClass();
        java.lang.reflect.Method method;
        try {
            method = c.getMethod("getTag");
            Object answer = method.invoke(nmsitem);
            return answer;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    public static ItemStack remove(ItemStack item, String key) {
        Object nmsitem = getNMSItemStack(item);
        if (nmsitem == null) {
            return null;
        }
        Object nbttag = getNBTTagCompound(nmsitem);
        if (nbttag == null) return null;
        java.lang.reflect.Method method;
        try {
            method = nbttag.getClass().getMethod("remove", String.class);
            method.invoke(nbttag, key );
            nmsitem = setNBTTag(nbttag, nmsitem);
            return getBukkitItemStack(nmsitem);
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return item;
    }
}