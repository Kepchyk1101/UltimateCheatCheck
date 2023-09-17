package com.kepchyk1101.ultimatecheatcheck.utils;

import com.kepchyk1101.ultimatecheatcheck.UltimateCheatCheck;
import me.clip.placeholderapi.PlaceholderAPI;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.Nullable;

public class ChatUtils {

    public static void sendMessage(CommandSender recipient, String message) {

        recipient.sendMessage(format(ConfigUtils.getString("messagesPrefix") + message,
                recipient instanceof Player ? (Player) recipient : null));

    }

    public static void sendTitle(Player player, String title, String subTitle, int fadeIn, int duration, int fadeOut) {

        player.sendTitle(format(title, player), format(subTitle, player), fadeIn, duration, fadeOut);

    }

    public static String format(String text, @Nullable Player player) {

        if (player != null && UltimateCheatCheck.isPlaceholderAPILoaded())
            return ChatColor.translateAlternateColorCodes('&', PlaceholderAPI.setPlaceholders(player, text));
        else
            return ChatColor.translateAlternateColorCodes('&', text);

    }

}