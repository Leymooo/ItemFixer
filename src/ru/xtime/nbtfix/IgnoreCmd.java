package ru.xtime.nbtfix;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import com.comphenix.protocol.wrappers.nbt.NbtCompound;
import com.comphenix.protocol.wrappers.nbt.NbtFactory;

public class IgnoreCmd implements CommandExecutor {
    private final Main plugin;
    public IgnoreCmd(Main Main) {
        this.plugin = Main;
    }
    @Override
    public boolean onCommand(final CommandSender sender, final Command cmd, final String commandLabel, final String[] args) {
        if (!(sender instanceof Player)) {
            sender.sendMessage("Данную команду может писать только игрок");
            return false;
        }
        Player p = (Player)sender;
        if (!p.hasPermission("itemfixer.bypass")) {
            sender.sendMessage("§cУ вас нет прав на эту команду");
            return false;
        }
        if (args.length == 0) {
            msgToPl(p,
                    "§a/itemfixer §c[add] §7- §aИгнорировать предмет от проверок",
                    "§a/itemfixer §c[remove] §7- §aСнова проверять предмет",
                    "§a/itemfixer §c[settag] [tag] §7- &aУстановить новый таг для игнорирования предметов");
            return false;
        }
        if (args[0].equalsIgnoreCase("add")) {
            if (setTag(p, true)) {
                msgToPl(p,
                        "§a[ItemFixer] §eТеперь этот предмет будет игнорироваться от проверок!",
                        "§a[ItemFixer] §cВнимание! Используйте на свой страх и риск!",
                        "§a[ItemFixer] §cДанный предмет могут раздюпать креативщики",
                        "              §cА также читеры могут спокойно изменять этот предмет!",
                        "§a[ItemFixer] §6Если это произошло, то просто поменяйте таг в конфиге или через команду."
                        );

            } else {
                msgToPl(p,
                        "§a[ItemFixer] §cНе получилось игнорировать предмет!",
                        "              §cСкорее всего предмет уже игнорируется"
                        );
            }
        }
        if (args[0].equalsIgnoreCase("remove")) {
            if (setTag(p, false)) {
                msgToPl(p,
                        "§a[ItemFixer] §eТеперь этот предмет снова проверяется!",
                        "§a[ItemFixer] §cДанный предмет уже мог быть раздюпан" , 
                        "§a[ItemFixer] §6Если вы уверены что предмет небыл раздюпан, то",
                        "§6              оставьте всё как есть. Если нет,",
                        "§6              то просто поменяйте таг в конфиге или через команду."
                        );
            } else {
                msgToPl(p,
                        "§a[ItemFixer] §cНе получилось убрать игнорирование предмета!",
                        "              §cСкорее всего предмет уже не игнорируется"
                        );
            }
        }
        if (args[0].equalsIgnoreCase("reload")) {
            plugin.config();
            p.sendMessage("§aКонфиг перезагружен");
        }
        if (args.length == 2 && args[0].equalsIgnoreCase("settag")) {
            plugin.ignoretag = args[1];
            plugin.getConfig().set("ignoreTag", args[1]);
            plugin.saveConfig();
            p.sendMessage("§aНовый таг установлен");
        }
        return false;
    }

    private void msgToPl(Player p, String... a) {
        for (String line : a) {
            p.sendMessage(line);
        }
    }
    @SuppressWarnings("deprecation")
    private boolean setTag(Player p, Boolean a) {
        try {
            Boolean b = false;
            ItemStack is;
            try {
                Class.forName("org.bukkit.inventory.PlayerInventory").getDeclaredMethod("getItemInMainHand");
                b = true;
            } catch (Exception e) { }
            if (b) {
                is = p.getInventory().getItemInMainHand();
            } else {
                is = p.getItemInHand();
            }
            if (is == null) return false;
            NbtCompound tag = (NbtCompound) NbtFactory.fromItemTag(is);
            if (a) {
                if (!tag.containsKey(plugin.ignoretag)) {
                    tag.put(plugin.ignoretag, "random msg");
                    return true;
                }
            } else {
                if (tag.containsKey(plugin.ignoretag)) {
                    tag.remove(plugin.ignoretag);
                    return true;
                }
            }
            return false;
        } catch (Exception e) {
            p.sendMessage("§a[ItemFixer] &cПроизошла ошибка, загляните в консоль и выложите эту ошибку сюда: &ehttp://rubukkit.org/threads/119485");
            e.printStackTrace();
            return false;
        }

    }
}
