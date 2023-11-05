package me.kepchyk1101.ultimatecheatcheck.util;

import me.clip.placeholderapi.PlaceholderAPI;
import me.kepchyk1101.ultimatecheatcheck.UltimateCheatCheck;
import net.kyori.adventure.platform.bukkit.BukkitAudiences;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.minimessage.MiniMessage;
import net.kyori.adventure.text.serializer.legacy.LegacyComponentSerializer;
import net.kyori.adventure.title.Title;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.time.Duration;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class ChatUtils {

    private static final Pattern HEX_PATTERN = Pattern.compile("&#[a-fA-F0-9]{6}");

    private static final MiniMessage MINI_MESSAGE = MiniMessage.miniMessage();
    private static final LegacyComponentSerializer LEGACY = LegacyComponentSerializer.legacyAmpersand();
    private static final BukkitAudiences AUDIENCES = UltimateCheatCheck.getInstance().getAudiences();

    public static void sendMessage(CommandSender recipient, String message) {

        if (UltimateCheatCheck.getInstance().isPlaceholderAPICompatibility()) {
            message = PlaceholderAPI.setPlaceholders(recipient instanceof Player ? (Player) recipient : null, message);
        }

        AUDIENCES.sender(recipient).sendMessage(
                Component.text()
                        .append(getComponentFromText(ConfigUtils.getString("messagesPrefix")))
                        .append(getComponentFromText(message))
                        .build()
        );

    }

    public static Component getComponentFromText(String text) {

        if (text.startsWith("[minimessage] ")) {
            return MINI_MESSAGE.deserialize(text.replaceFirst("\\[minimessage] ", ""));
        } else {
            return LEGACY.deserialize(text);
        }

    }

    public static void sendTitle(Player recipient, String title, String subTitle,
                                 long fadeIn, long stay, long fadeOut) {

        AUDIENCES.player(recipient).showTitle(
                Title.title(
                        getComponentFromText(title),
                        getComponentFromText(subTitle),
                        Title.Times.times(
                                Duration.ofSeconds(fadeIn),
                                Duration.ofSeconds(stay),
                                Duration.ofSeconds(fadeOut)
                        )
                )
        );

    }

    //ToDo: заменить на что-то получше (используется для dispatchCommand)
    public static String format(String message, Player player) {

        if (UltimateCheatCheck.getInstance().isPlaceholderAPICompatibility()) {
            message = PlaceholderAPI.setPlaceholders(player, message);
        }

        return colorize(message);

    }

    //ToDo: заменить на что-то получше (используется для dispatchCommand)
    private static String colorize(String message) {

        // hex colors (1.16+)
        if (UltimateCheatCheck.getInstance().getServerVersion() == ServerVersion.V1_16_orHigher) {

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