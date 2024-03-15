package me.kepchyk1101.ultimatecheatcheck.util;

import org.bukkit.Location;
import org.bukkit.configuration.file.FileConfiguration;

import java.util.List;

public class ConfigUtils {

    private static FileConfiguration config, messages;

    public static int getInt(String path) {
        return config.getInt(path);
    }

    public static String getString(String path) {
        return config.getString(path);
    }

    public static List<String> getStrings(String path) {
        return config.getStringList(path);
    }

    public static boolean getBoolean(String path) {
        return config.getBoolean(path);
    }

    public static String getMessage(String path) {
        String message = messages.getString(path);
        return message != null ? message : path;
    }

    public static List<String> getMessages(String path) {
        return messages.getStringList(path);
    }

    public static Location getLocation(String path) {
        return config.getLocation(path);
    }

    public static void setConfigs(FileConfiguration config, FileConfiguration messages) {
        ConfigUtils.config = config;
        ConfigUtils.messages = messages;
    }

}