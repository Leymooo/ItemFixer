package ru.xtime.nbtfix;

import org.bukkit.GameMode;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import com.comphenix.protocol.PacketType;
import com.comphenix.protocol.events.ListenerPriority;
import com.comphenix.protocol.events.PacketAdapter;
import com.comphenix.protocol.events.PacketEvent;

public class NBTCreatListener extends PacketAdapter {
    public NBTCreatListener(Main plugin) {
        super(plugin, ListenerPriority.HIGHEST, PacketType.Play.Client.SET_CREATIVE_SLOT);
    }

    @Override
    public void onPacketReceiving(PacketEvent event) {
        if (event.isCancelled()) return;
        final Player p = event.getPlayer();
        if (p.getGameMode() != GameMode.CREATIVE) return;
        if (p.hasPermission("itemfixer.bypass")) return;
        ItemStack stack = event.getPacket().getItemModifier().read(0);
        if (stack == null) return;
        if (((Main) getPlugin()).isExploit(stack, p.getWorld().getName().toLowerCase())){
            p.sendMessage("§cЧитерские вещи запрещены! Если вы продолжите, вы будете забанены!");
            p.updateInventory();
        }
    }
}
