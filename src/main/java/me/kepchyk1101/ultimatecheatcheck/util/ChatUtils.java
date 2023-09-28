package me.kepchyk1101.ultimatecheatcheck.util;

import me.clip.placeholderapi.PlaceholderAPI;
import me.kepchyk1101.ultimatecheatcheck.UltimateCheatCheck;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.Nullable;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class ChatUtils {

    private static final Pattern HEX_PATTERN = Pattern.compile("&#[a-fA-F0-9]{6}");
    private static final ServerVersion SERVER_VERSION = UltimateCheatCheck.getInstance().getServerVersion();

    public static void sendMessage(CommandSender recipient, String message) {

        recipient.sendMessage(format(ConfigUtils.getString("messagesPrefix") + message,
                recipient instanceof Player ? (Player) recipient : null));

    }

    public static void sendTitle(Player recipient, String title, String subTitle, int fadeIn, int duration, int fadeOut) {

        recipient.sendTitle(format(title, recipient), format(subTitle, recipient), fadeIn, duration, fadeOut);

    }

    public static String format(String message, @Nullable Player player) {

        if (player != null && UltimateCheatCheck.getInstance().isPlaceholderAPILoaded()) {
            message = PlaceholderAPI.setPlaceholders(player, message);
        }

        return colorize(message);

    }

    private static String colorize(String message) {

        // hex colors (1.16+)
        if (SERVER_VERSION == ServerVersion.V1_16_orHigher) {

            Matcher match = HEX_PATTERN.matcher(message);
            while (match.find()) {
                String color = message.substring(match.start() + 1, match.end());
                message = message.replace("&" + color, net.md_5.bungee.api.ChatColor.of(color) + "");
                match = HEX_PATTERN.matcher(message);
            }
            return ChatColor.translateAlternateColorCodes('&', message.replace("{", "").replace("}", ""));

        }

        // default mc colors
        return ChatColor.translateAlternateColorCodes('&', message);

    }

}