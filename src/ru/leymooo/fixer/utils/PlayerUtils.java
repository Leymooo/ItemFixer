package ru.leymooo.fixer.utils;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import com.comphenix.net.sf.cglib.proxy.Factory;
import com.comphenix.protocol.events.PacketEvent;


public class PlayerUtils {
    
    private static boolean isNewProtocolLib = false;
    
    static {
        try {
            Class.forName("com.comphenix.protocol.injector.server.TemporaryPlayer");
            isNewProtocolLib = true;
        } catch (ClassNotFoundException e) {}
    }
    
    public static Player getPlayerFromEvent(PacketEvent event) {
        Player eventPlayer = event.getPlayer();
        
        if (eventPlayer == null || !eventPlayer.isOnline()) return null;
        
        if (isNewProtocolLib && event.isPlayerTemporary()) return null;
        
        String playerName = eventPlayer.getName();
        
        if (playerName == null || playerName.isEmpty()) return null; //Всякое бывает в этой жизни.
        
        Player bukkitPlayer = Bukkit.getPlayerExact(playerName);
        
        if (bukkitPlayer == null || !bukkitPlayer.isOnline()) return null;
        
        if ("InjectorContainer".equals(bukkitPlayer.getClass().getSimpleName()) || bukkitPlayer instanceof Factory) return null;
        
        return bukkitPlayer;
    }
}
