package ru.leymooo.fixer;

import io.netty.buffer.ByteBuf;

import java.util.HashMap;

import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import com.comphenix.protocol.PacketType;
import com.comphenix.protocol.events.ListenerPriority;
import com.comphenix.protocol.events.PacketAdapter;
import com.comphenix.protocol.events.PacketContainer;
import com.comphenix.protocol.events.PacketEvent;
import com.google.common.base.Charsets;

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
        ItemStack stack = event.getPacket().getItemModifier().readSafely(0);
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
        String channel = event.getPacket().getStrings().readSafely(0);
        if ("MC|BEdit".equals(channel) || "MC|BSign".equals(channel)) {
            this.cancel.put(p, System.currentTimeMillis());
        } else if ("REGISTER".equals(channel)) {
            checkRegisterChannel(event, p);
        }
    }
    /**
     * @author justblender
     */
    private void checkRegisterChannel(PacketEvent event, Player p) {
        int channelsSize = p.getListeningPluginChannels().size();
        final PacketContainer container = event.getPacket();
        final ByteBuf buffer = (container.getSpecificModifier(ByteBuf.class).read(0)).copy();
        final String[] channels =  buffer.toString(Charsets.UTF_8).split("\0");
        for (int i = 0;i<channels.length;i++) {
            if (++channelsSize > 120) {
                event.setCancelled(true);
                Bukkit.getScheduler().runTask((Main)getPlugin(), ()->p.kickPlayer("Too many channels registered (max: 120)"));
                buffer.release();
                return;
            }
        }
        buffer.release();
    }
    private boolean needCancel(Player p) {
        return this.cancel.containsKey(p) && (1200 - (System.currentTimeMillis() - this.cancel.get(p))) > 0;
    }
}
