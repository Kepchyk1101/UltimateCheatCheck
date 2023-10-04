package me.kepchyk1101.ultimatecheatcheck.config;

import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;
import java.util.logging.Logger;

public class Localization {

    public static final String PREFIX = "lang/messages_";
    public static final String EXTENSION = ".yml";

    private final JavaPlugin plugin;
    private final File pluginDataFolder;
    private final Logger logger;

    public Localization(JavaPlugin plugin) {
        this.plugin = plugin;
        this.pluginDataFolder = this.plugin.getDataFolder();
        this.logger = this.plugin.getLogger();
    }

    public YamlConfiguration loadLocalizedMessages() {

        saveDefaultLocalizations("en", "ru", "ua", "hu");

        String langFilePath = getLocalePath(plugin.getConfig().getString("language"));
        if (!new File(pluginDataFolder, langFilePath).exists()) {

            logger.info("§6The localization file specified in the config was not found!");
            logger.info("§6The standard localization file will be used ...");

            langFilePath = getLocalePath("en"); // default language

        }

        return YamlConfiguration.loadConfiguration(new File(pluginDataFolder, langFilePath));

    }

    private void saveDefaultLocalizations(String... localeCodes) {
        for (String localeCode : localeCodes) {
            final String localeFilePath = getLocalePath(localeCode);
            if (!new File(pluginDataFolder, localeFilePath).exists()) {
                plugin.saveResource(localeFilePath, false);
            }
        }
    }

    private String getLocalePath(String localeCode) {
        return PREFIX + localeCode + EXTENSION;
    }

}
