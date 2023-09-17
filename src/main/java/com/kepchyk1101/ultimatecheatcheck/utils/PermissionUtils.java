package com.kepchyk1101.ultimatecheatcheck.utils;

import org.bukkit.command.CommandSender;

public class PermissionUtils {

    public static boolean hasPermission(CommandSender target, String... permissions) {

        for (String permission : permissions)
            if (target.hasPermission(permission))
                return true;

        ChatUtils.sendMessage(target, ConfigUtils.getMessage("errors.noPermission"));
        return false;

    }

}