package com.kepchyk1101.ultimatecheatcheck.utils;

import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class PlayerUtils {

    public static boolean isPlayer(CommandSender commandSender) {

        if (commandSender instanceof Player)
            return true;

        ChatUtils.sendMessage(commandSender, ConfigUtils.getMessage("errors.commandCanUsedOnlyByPlayer"));
        return false;

    }

    public static boolean hasPermission(CommandSender target, String... permissions) {

        for (String permission : permissions)
            if (target.hasPermission(permission))
                return true;

        ChatUtils.sendMessage(target, ConfigUtils.getMessage("errors.noPermission"));
        return false;

    }

}