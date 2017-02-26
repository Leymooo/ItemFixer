package ru.xtime.nbtfix;

import java.util.HashMap;
import java.util.HashSet;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerItemHeldEvent;
import org.bukkit.event.player.PlayerPickupItemEvent;
import org.bukkit.inventory.ItemStack;

public class TextureFix implements Listener {
    HashMap<Material,Integer> limit = new HashMap<Material, Integer>();
    HashSet<Material> ignore = new HashSet<Material>();

    public TextureFix() {
        String version = Bukkit.getServer().getClass().getPackage().getName().replace(".",  ",").split(",")[3];
        //Вроде все предменты что имеют SubId
        //Material, MaxSubId
        limit.put(Material.STONE, 6);
        limit.put(Material.DIRT, 2);
        limit.put(Material.WOOD, 5);
        limit.put(Material.SAPLING, 5);
        limit.put(Material.SAND, 1);
        limit.put(Material.LOG, 3);
        limit.put(Material.LEAVES, 3);
        limit.put(Material.SPONGE, 1);
        limit.put(Material.SANDSTONE, 2);
        limit.put(Material.LONG_GRASS, 2);
        limit.put(Material.WOOL, 15);
        limit.put(Material.RED_ROSE, 8);
        limit.put(Material.DOUBLE_STEP, 7);
        limit.put(Material.STEP, 7);
        limit.put(Material.STAINED_GLASS, 15);
        limit.put(Material.MONSTER_EGGS, 5);
        limit.put(Material.SMOOTH_BRICK, 3);
        limit.put(Material.WOOD_DOUBLE_STEP, 5);
        limit.put(Material.WOOD_STEP, 5);
        limit.put(Material.COBBLE_WALL, 1);
        limit.put(Material.QUARTZ_BLOCK, 2);
        limit.put(Material.STAINED_CLAY, 15);
        limit.put(Material.STAINED_GLASS, 15);
        limit.put(Material.STAINED_GLASS_PANE, 15);
        limit.put(Material.LEAVES_2, 1);
        limit.put(Material.LOG_2, 1);
        limit.put(Material.PRISMARINE, 2);
        limit.put(Material.CARPET, 15);
        limit.put(Material.DOUBLE_PLANT, 5);
        limit.put(Material.RED_SANDSTONE, 2);
        limit.put(Material.COAL, 1);
        limit.put(Material.RAW_FISH, 3);
        limit.put(Material.COOKED_FISH, 1);
        limit.put(Material.INK_SACK, 15);
        limit.put(Material.SKULL_ITEM, 5);
        limit.put(Material.GOLDEN_APPLE, 1);
        limit.put(Material.BANNER, 15);
        limit.put(Material.ANVIL, 2);
        //Предметы с прочностью.
        ignore.add(Material.MAP);
        ignore.add(Material.CARROT_STICK);
        ignore.add(Material.BOW);
        ignore.add(Material.FISHING_ROD);
        ignore.add(Material.FLINT_AND_STEEL);
        ignore.add(Material.SHEARS);
        if (version.startsWith("v1_8_R")) {
            ignore.add(Material.MONSTER_EGG);
            ignore.add(Material.POTION);
        }
        if (Material.matchMaterial("SHIELD") != null) {
            ignore.add(Material.SHIELD);
            ignore.add(Material.ELYTRA);
        }
        //Деревянные инструменты
        ignore.add(Material.WOOD_SPADE);
        ignore.add(Material.WOOD_PICKAXE);
        ignore.add(Material.WOOD_AXE);
        ignore.add(Material.WOOD_SWORD);
        ignore.add(Material.WOOD_HOE);
        
        //Золотые инструменты
        ignore.add(Material.GOLD_SPADE);
        ignore.add(Material.GOLD_PICKAXE);
        ignore.add(Material.GOLD_AXE);
        ignore.add(Material.GOLD_SWORD);
        ignore.add(Material.GOLD_HOE);
        
        //Каменные инструменты
        ignore.add(Material.STONE_SPADE);
        ignore.add(Material.STONE_PICKAXE);
        ignore.add(Material.STONE_AXE);
        ignore.add(Material.STONE_SWORD);
        ignore.add(Material.STONE_HOE);
        
        //Железные инструменты
        ignore.add(Material.IRON_SPADE);
        ignore.add(Material.IRON_PICKAXE);
        ignore.add(Material.IRON_AXE);
        ignore.add(Material.IRON_SWORD);
        ignore.add(Material.IRON_HOE);
        
        //Алмазные инструменты
        ignore.add(Material.DIAMOND_SPADE);
        ignore.add(Material.DIAMOND_PICKAXE);
        ignore.add(Material.DIAMOND_AXE);
        ignore.add(Material.DIAMOND_SWORD);
        ignore.add(Material.DIAMOND_HOE);

        //Разная броня
        ignore.add(Material.LEATHER_BOOTS);
        ignore.add(Material.LEATHER_CHESTPLATE);
        ignore.add(Material.LEATHER_HELMET);
        ignore.add(Material.LEATHER_LEGGINGS);

        ignore.add(Material.IRON_BOOTS);
        ignore.add(Material.IRON_CHESTPLATE);
        ignore.add(Material.IRON_HELMET);
        ignore.add(Material.IRON_LEGGINGS);

        ignore.add(Material.GOLD_BOOTS);
        ignore.add(Material.GOLD_CHESTPLATE);
        ignore.add(Material.GOLD_HELMET);
        ignore.add(Material.GOLD_LEGGINGS);

        ignore.add(Material.DIAMOND_BOOTS);
        ignore.add(Material.DIAMOND_CHESTPLATE);
        ignore.add(Material.DIAMOND_HELMET);
        ignore.add(Material.DIAMOND_LEGGINGS);

    }
    @EventHandler(priority = EventPriority.LOWEST, ignoreCancelled = false)
    public void onHold(PlayerItemHeldEvent e) {
        ItemStack it = e.getPlayer().getInventory().getItem(e.getNewSlot());
        if (isInvalide(it)) {
            e.setCancelled(true);
            e.getPlayer().getInventory().remove(it);
            e.getPlayer().updateInventory();
        }
    }
    @EventHandler(priority = EventPriority.LOWEST, ignoreCancelled = false)
    public void onInteract(PlayerInteractEvent e) {
        ItemStack it = e.getItem();
        if (isInvalide(it)) {
            e.setCancelled(true);
            e.getPlayer().getInventory().remove(it);
            e.getPlayer().updateInventory();
        }
    }
    @EventHandler(priority = EventPriority.LOWEST, ignoreCancelled = false)
    public void onClick(InventoryClickEvent e) {
        ItemStack it = e.getCurrentItem();
        if (e.getWhoClicked().getType() == EntityType.PLAYER && isInvalide(it)) {
            e.setCancelled(true);
            e.getWhoClicked().getInventory().remove(it);
            ((Player)e.getWhoClicked()).updateInventory();
        }
    }
    @EventHandler(priority = EventPriority.LOWEST, ignoreCancelled = false)
    public void onPickup(PlayerPickupItemEvent e) {
        ItemStack it = e.getItem().getItemStack();
        if (isInvalide(it)) {
            e.setCancelled(true);
            e.getItem().remove();
        }
    }

    private boolean isInvalide(ItemStack it) {
        if (it != null && it.getType()!=Material.AIR) {
            //Игнорим обычные предметы
            if (it.getDurability() != 0) {
                //Чекаем есть ли этот предмет в лимитах.
                if (limit.containsKey(it.getType())) {
                    //Проверяем subid чтобы был не меньше нуля, и не превышал лимит.
                    if (it.getDurability() < 0 || it.getDurability() > limit.get(it.getType())) {
                        return true;
                    }
                    //У предмета всё норм.
                    return false;
                }
                //Чекам на предметы которое могут ломаться. 
                if (ignore.contains(it.getType())) {
                    return false;
                }
                //У нас кривой предмет(
                return true;
            }
        }
        return false;
    }
}
