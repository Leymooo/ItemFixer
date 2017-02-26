package ru.xtime.nbtfix;

import java.util.HashMap;

import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import com.comphenix.protocol.PacketType;
import com.comphenix.protocol.events.ListenerPriority;
import com.comphenix.protocol.events.PacketAdapter;
import com.comphenix.protocol.events.PacketEvent;

public class NBTCreatListener extends PacketAdapter {
    private HashMap<Player, Integer> needCancel = new HashMap<Player, Integer>();
    public NBTCreatListener(Main plugin) {
        super(plugin, ListenerPriority.HIGHEST, PacketType.Play.Client.SET_CREATIVE_SLOT);
        Bukkit.getScheduler().scheduleSyncRepeatingTask(plugin, new Runnable() {
            @Override
            public void run() {
                for (Player p : needCancel.keySet()) {
                    int i = needCancel.get(p);
                    if (i >= 5) {
                        needCancel.remove(p);
                        continue;
                    }
                    needCancel.put(p, i+1);
                }

            }
        }, 20, 20);
    }

    @Override
    public void onPacketReceiving(PacketEvent event) {
        if (event.isCancelled()) return;
        final Player p = event.getPlayer();
        if (needCancel.containsKey(p)) {
            event.setCancelled(true);
            p.updateInventory();
            return;
        }
        if (p.getGameMode() != GameMode.CREATIVE) return;
        if (p.hasPermission("itemfixer.bypass")) return;
        ItemStack stack = event.getPacket().getItemModifier().read(0);
        if (stack == null) return;
        if (((Main) getPlugin()).isExploit(stack, p.getWorld().getName().toLowerCase())){
            needCancel.put(p, 0);
            p.updateInventory();
        }
    }
}
