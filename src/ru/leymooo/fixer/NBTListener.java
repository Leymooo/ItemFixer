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
import com.comphenix.protocol.injector.server.TemporaryPlayerFactory;
import com.google.common.base.Charsets;

public class NBTListener extends PacketAdapter {
    public static HashMap<Player, Long> cancel;
    private String version;
    public NBTListener(Main plugin, String version) {
        super(plugin, ListenerPriority.HIGHEST, PacketType.Play.Client.SET_CREATIVE_SLOT, PacketType.Play.Client.HELD_ITEM_SLOT, PacketType.Play.Client.CUSTOM_PAYLOAD);
        this.version = version;
        cancel = new HashMap<Player, Long>();
    }
    
    @Override
    public void onPacketReceiving(PacketEvent event) {
        if (event.isCancelled()) return;
        Player p = this.getPlayer(event.getPlayer());
        if (p == null || !p.isOnline()) return;
        if (this.needCancel(p)) {
            event.setCancelled(true);
            return;
        }
        if (event.getPacketType() == PacketType.Play.Client.SET_CREATIVE_SLOT && p.getGameMode() == GameMode.CREATIVE) {
            this.proccessSetCreativeSlot(event, p);
        } else if (event.getPacketType() == PacketType.Play.Client.HELD_ITEM_SLOT) {
            this.proccessHeldItemSlot(event, p);
        } else if (event.getPacketType() == PacketType.Play.Client.CUSTOM_PAYLOAD && !p.hasPermission("itemfixer.bypass.packet")) {
            this.proccessCustomPayload(event, p);
        }
    }

    private void proccessSetCreativeSlot(PacketEvent event, Player p) {
        ItemStack stack = event.getPacket().getItemModifier().readSafely(0);
        if (((Main) getPlugin()).checkItem(stack, p)){
            cancel.put(p, System.currentTimeMillis());
            p.updateInventory();
        }
    }
    
    private void proccessHeldItemSlot(PacketEvent event, Player p) {
        Integer i = event.getPacket().getIntegers().readSafely(0);
        ItemStack stack = (i == null) ? null : p.getInventory().getItem(i.shortValue());
        if (((Main) getPlugin()).checkItem(stack, p)){
            p.updateInventory();
        }
    }
    
    private void proccessCustomPayload(PacketEvent event, Player p) {
        String channel = event.getPacket().getStrings().readSafely(0);
        if (("MC|BEdit".equals(channel) || "MC|BSign".equals(channel)) && !version.startsWith("v1_11_R")) {
            cancel.put(p, System.currentTimeMillis());
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
        return cancel.containsKey(p) && (1200 - (System.currentTimeMillis() - cancel.get(p))) > 0;
    }
    
    private Player getPlayer(Player p) {
        return (p instanceof TemporaryPlayerFactory) ? Bukkit.getPlayerExact(p.getName()) : p;
    }
}
