package ru.xtime.nbtfix;

import com.comphenix.protocol.PacketType;
import com.comphenix.protocol.events.ListenerPriority;
import com.comphenix.protocol.events.PacketAdapter;
import com.comphenix.protocol.events.PacketEvent;

import org.bukkit.GameMode;
import org.bukkit.inventory.ItemStack;

public class NBTCreatListener extends PacketAdapter {
    public NBTCreatListener(Main plugin) {
        super(plugin, ListenerPriority.HIGHEST, PacketType.Play.Client.SET_CREATIVE_SLOT);
    }

    @Override
    public void onPacketReceiving(PacketEvent event) {
        if (event.isCancelled()) return;
        if (event.getPlayer().getGameMode() != GameMode.CREATIVE) return;
        if (event.getPlayer().hasPermission("itemfixer.bypass")) return;
        ItemStack stack = event.getPacket().getItemModifier().read(0);
        if (((Main) getPlugin()).isExploit(stack)){
            event.getPlayer().sendMessage("§cЧитерские вещи запрещены! Если вы продолжите, вы будете забанены!");
        }
    }
}
