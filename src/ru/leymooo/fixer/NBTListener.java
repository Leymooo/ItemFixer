package ru.leymooo.fixer;

import java.util.HashMap;

import org.bukkit.GameMode;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import com.comphenix.protocol.PacketType;
import com.comphenix.protocol.events.ListenerPriority;
import com.comphenix.protocol.events.PacketAdapter;
import com.comphenix.protocol.events.PacketEvent;

public class NBTListener extends PacketAdapter {
    private HashMap<Player, Long> cancel;
    private String version;
    public NBTListener(Main plugin, String version) {
        super(plugin, ListenerPriority.HIGHEST, PacketType.Play.Client.SET_CREATIVE_SLOT, PacketType.Play.Client.HELD_ITEM_SLOT, PacketType.Play.Client.CUSTOM_PAYLOAD);
        this.version = version;
        this.cancel = new HashMap<Player, Long>();
    }
    @Override
    public void onPacketReceiving(PacketEvent event) {
        if (event.isCancelled()) return;
        Player p = event.getPlayer();
        if (p == null || !p.isOnline()) return;
        if (this.needCancel(p)) {
            event.setCancelled(true);
            return;
        }
        if (p.hasPermission("itemfixer.bypass")) return;
        if (event.getPacketType() == PacketType.Play.Client.SET_CREATIVE_SLOT && p.getGameMode() == GameMode.CREATIVE) {
            this.proccessSetCreativeSlot(event, p);
        } else if (event.getPacketType() == PacketType.Play.Client.HELD_ITEM_SLOT) {
            this.proccessHeldItemSlot(event, p);
        } else if (event.getPacketType() == PacketType.Play.Client.CUSTOM_PAYLOAD && !version.startsWith("v1_11_R")) {
            this.proccessCustomPayload(event, p);
        }
    }
    
    private void proccessSetCreativeSlot(PacketEvent event, Player p) {
        ItemStack stack = event.getPacket().getItemModifier().read(0);
        if (((Main) getPlugin()).checkItem(stack, p.getWorld().getName().toLowerCase())){
            this.cancel.put(p, System.currentTimeMillis());
            p.updateInventory();
        }
    }
    private void proccessHeldItemSlot(PacketEvent event, Player p) {
        ItemStack stack = p.getInventory().getItem(event.getPacket().getIntegers().readSafely(0).shortValue());
        if (((Main) getPlugin()).checkItem(stack, p.getWorld().getName().toLowerCase())){
            p.updateInventory();
        }
    }
    private void proccessCustomPayload(PacketEvent event, Player p) {
        String channel = event.getPacket().getStrings().read(0);
        if ((channel.equalsIgnoreCase("MC|BEdit") || channel.equalsIgnoreCase("MC|BSign"))) {
            this.cancel.put(p, System.currentTimeMillis());
        }

    }
    private boolean needCancel(Player p) {
        return this.cancel.containsKey(p) && (3000 - (System.currentTimeMillis() - this.cancel.get(p))) > 0;
    }
}
