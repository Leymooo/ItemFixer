package ru.xtime.nbtfix;

import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import com.comphenix.protocol.PacketType;
import com.comphenix.protocol.events.PacketAdapter;
import com.comphenix.protocol.events.PacketEvent;

public class NBTHeldItemListener extends PacketAdapter {
    public NBTHeldItemListener(Main plugin) {
        super(plugin, PacketType.Play.Client.HELD_ITEM_SLOT);
    }

    @Override
    public void onPacketReceiving(PacketEvent event) {
        if (event.isCancelled()) return;
        final Player p = event.getPlayer();
        if (p == null) return;
        if (!p.isOnline()) return;
        if (p.hasPermission("itemfixer.bypass")) return;
        ItemStack stack = p.getInventory().getItem(event.getPacket().getIntegers().readSafely(0).shortValue());
        if (stack == null) return;
        if (((Main) getPlugin()).isExploit(stack, p.getWorld().getName().toLowerCase())){
            p.updateInventory();
        }
    }
}
