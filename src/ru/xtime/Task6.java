package ru.xtime;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

public class Task6 implements Runnable {
	public void run() {
		System.out.print(ChatColor.GOLD + "[ItemFixer]" + ChatColor.RED +"Найдено новое обновление!");
		System.out.print(ChatColor.GOLD + "[ItemFixer]" + ChatColor.RED +"http://rubukkit.org/threads/119485/");
		for (final Player p : Bukkit.getServer().getOnlinePlayers()) {
			if (p.hasPermission("itemfixer.checkupdate")) {
				p.sendMessage(ChatColor.GOLD + "[ItemFixer]" + ChatColor.RED +"Найдено новое обновление!");
				p.sendMessage(ChatColor.GOLD + "[ItemFixer]" + ChatColor.RED +"http://rubukkit.org/threads/119485/");
				}
		}
	}
}