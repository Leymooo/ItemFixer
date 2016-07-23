package ru.xtime.nbtfix;

import org.bukkit.inventory.ItemStack;

import com.comphenix.protocol.PacketType;
import com.comphenix.protocol.events.PacketAdapter;
import com.comphenix.protocol.events.PacketEvent;

public class NBTArmListener extends PacketAdapter {
    public NBTArmListener(Main plugin) {
        super(plugin, PacketType.Play.Client.HELD_ITEM_SLOT);
    }

    @Override
    public void onPacketReceiving(PacketEvent event) {
        if (event.getPlayer().hasPermission("itemfixer.bypass")) return;
        ItemStack stack = event.getPlayer().getInventory().getItem(event.getPacket().getIntegers().readSafely(0).shortValue());
        if (stack == null) return;
        if (((Main) getPlugin()).isExploit(stack, event.getPlayer())){
            event.getPlayer().sendMessage("§cЧитерские вещи запрещены! Если вы продолжите, вы будете забанены!");
        }
    }
}
