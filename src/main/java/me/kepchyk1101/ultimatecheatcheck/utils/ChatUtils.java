package me.kepchyk1101.ultimatecheatcheck.utils;

import me.clip.placeholderapi.PlaceholderAPI;
import me.kepchyk1101.ultimatecheatcheck.UltimateCheatCheck;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.Nullable;

public class ChatUtils {

    public static void sendMessage(CommandSender recipient, String message) {

        recipient.sendMessage(format(ConfigUtils.getString("messagesPrefix") + message,
                recipient instanceof Player ? (Player) recipient : null));

    }

    public static void sendTitle(Player recipient, String title, String subTitle, int fadeIn, int duration, int fadeOut) {

        recipient.sendTitle(format(title, recipient), format(subTitle, recipient), fadeIn, duration, fadeOut);

    }

    public static String format(String text, @Nullable Player player) {

        if (player != null && UltimateCheatCheck.getInstance().isPlaceholderAPILoaded())
            return ChatColor.translateAlternateColorCodes('&', PlaceholderAPI.setPlaceholders(player, text));
        else
            return ChatColor.translateAlternateColorCodes('&', text);

    }

}