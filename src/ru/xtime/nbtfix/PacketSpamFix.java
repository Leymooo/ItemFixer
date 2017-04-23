package ru.xtime.nbtfix;

import org.bukkit.entity.Player;

import com.comphenix.protocol.PacketType;
import com.comphenix.protocol.events.ListenerPriority;
import com.comphenix.protocol.events.PacketAdapter;
import com.comphenix.protocol.events.PacketContainer;
import com.comphenix.protocol.events.PacketEvent;

public class PacketSpamFix extends PacketAdapter {
    public PacketSpamFix(Main plugin) {
        super(plugin, ListenerPriority.HIGHEST, PacketType.Play.Client.CUSTOM_PAYLOAD);
    }

    @Override
    public void onPacketReceiving(PacketEvent event) {
        if (event.isCancelled()) return;
        final Player p = event.getPlayer();
        if (p==null) return;
        if (NBTCreatListener.needCancel(p)) {
            NBTCreatListener.cancel.put(p, System.currentTimeMillis());
            event.setCancelled(true);
            return;
        }
        PacketContainer packet = event.getPacket();
        String channel = packet.getStrings().read(0);
        if (channel.equalsIgnoreCase("MC|BEdit") || channel.equalsIgnoreCase("MC|BSign")) {
            if (!NBTCreatListener.needCancel(p)) {
                NBTCreatListener.cancel.put(p, System.currentTimeMillis());
                return;
            }
            event.setCancelled(true);
        }

    }
}

