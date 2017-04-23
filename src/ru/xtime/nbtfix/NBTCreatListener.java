package ru.xtime.nbtfix;

import java.util.HashMap;

import org.bukkit.GameMode;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import com.comphenix.protocol.PacketType;
import com.comphenix.protocol.events.ListenerPriority;
import com.comphenix.protocol.events.PacketAdapter;
import com.comphenix.protocol.events.PacketEvent;

public class NBTCreatListener extends PacketAdapter {
    public static HashMap<Player, Long> cancel;
    public NBTCreatListener(Main plugin) {
        super(plugin, ListenerPriority.HIGHEST, PacketType.Play.Client.SET_CREATIVE_SLOT);
        cancel = new HashMap<Player, Long>();
    }

    @Override
    public void onPacketReceiving(PacketEvent event) {
        if (event.isCancelled()) return;
        final Player p = event.getPlayer();
        if (needCancel(p)) {
            event.setCancelled(true);
            p.updateInventory();
            return;
        }
        if (p.getGameMode() != GameMode.CREATIVE) return;
        if (p.hasPermission("itemfixer.bypass")) return;
        ItemStack stack = event.getPacket().getItemModifier().read(0);
        if (stack == null) return;
        if (((Main) getPlugin()).checkItem(stack, p.getWorld().getName().toLowerCase())){
            cancel.put(p, System.currentTimeMillis());
            p.updateInventory();
        }
    }
    public static boolean needCancel(Player p) {
        if (cancel.containsKey(p)) {
            Long lastSent = cancel.get(p);
            System.out.print(lastSent + " | "+ System.currentTimeMillis());
            Long ignoreTime = 3000 - (System.currentTimeMillis() - lastSent);
            System.out.print(ignoreTime);
            if (ignoreTime > 0) {
                return true;
            } else {
                cancel.remove(p);
                return false;
            }
        }
        return false;
    }
}
