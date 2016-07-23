package ru.xtime.nbtfix;

import com.comphenix.protocol.PacketType;
import com.comphenix.protocol.events.PacketAdapter;
import com.comphenix.protocol.events.PacketEvent;
import org.bukkit.inventory.ItemStack;

public class NBTFixListener extends PacketAdapter {
    public NBTFixListener(Main plugin) {
        super(plugin, PacketType.Play.Client.SET_CREATIVE_SLOT);
    }

    @Override
    public void onPacketReceiving(PacketEvent event) {
        ItemStack stack = event.getPacket().getItemModifier().read(0);
        if (((Main) getPlugin()).isExploit(stack)) {
            event.getPlayer().sendMessage("§cЧитерские вещи запрещены! Если вы продолжите, вы будете забанены!");
        }
    }
}
