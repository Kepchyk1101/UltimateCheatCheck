package me.kepchyk1101.ultimatecheatcheck;

import me.kepchyk1101.ultimatecheatcheck.cheatcheck.CheatCheckManager;
import me.kepchyk1101.ultimatecheatcheck.command.UCCCommand;
import me.kepchyk1101.ultimatecheatcheck.listeners.CheckListeners;
import me.kepchyk1101.ultimatecheatcheck.util.ConfigUtils;
import me.kepchyk1101.ultimatecheatcheck.util.RecoveryController;
import me.kepchyk1101.ultimatecheatcheck.util.ServerVersion;
import me.kepchyk1101.ultimatecheatcheck.util.UpdateChecker;
import org.bstats.bukkit.Metrics;
import org.bukkit.command.PluginCommand;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.event.HandlerList;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;
import java.util.logging.Logger;

public final class UltimateCheatCheck extends JavaPlugin {

    private static UltimateCheatCheck instance;
    private Logger logger;
    private boolean isPlaceholderAPILoaded;
    private ServerVersion serverVersion;
    private CheckListeners checkListeners;
    private RecoveryController recoveryController;

    private FileConfiguration messages;
    private FileConfiguration config;

    @Override
    public void onEnable() {

        instance = this;
        logger = getLogger();
        serverVersion = checkServerVersion();
        checkListeners = new CheckListeners();

        // Configuration routine
        saveDefaultConfig();
        config = getConfig();
        loadConfigs();

        // If the server was shut down incorrectly, restore all damage
        recoveryAfterShutdownCheck();

        // Check for integrations on the server
        loadIntegrations();

        // Register basic command and subcommands
        registerBasicCommand();

        // Checking and notifying about plugin updates
        if (ConfigUtils.getBoolean("checkUpdates")) {
            new UpdateChecker(this, 112711).checkPluginUpdates();
        }

        // Statistics
        new Metrics(this, 19805);

        logger.info("Plugin launched successfully!");

    }

    @Override
    public void onDisable() {

        // Correct completion of all checks during a normal server shutdown
        CheatCheckManager.completionAllChecks();
        HandlerList.unregisterAll(checkListeners);

        // Clear recovery file
        FileConfiguration recoveryConfig = recoveryController.getConfig();
        for (String key : recoveryConfig.getConfigurationSection("checks").getKeys(false))
            recoveryConfig.set("checks." + key, null);
        recoveryController.saveConfig();

    }

    private void recoveryAfterShutdownCheck() {

        recoveryController = new RecoveryController(this, new File(getDataFolder(), "recovery.yml"));
        if (recoveryController.isNeedRecovery()) {
            recoveryController.startRecovery();
        }

    }

    private ServerVersion checkServerVersion() {

        // A little stupid, here I check it this way, and a little lower it’s different :)

        final String version = getServer().getClass().getPackage().getName().split("\\.")[3];
        final String subVersion = version.replace("v1_", "").replaceAll("_R\\d", "");
        if (Integer.parseInt(subVersion) >= 16) {
            return ServerVersion.V1_16_orHigher;
        }

        try {
            Class.forName("org.bukkit.util.BoundingBox");
            return ServerVersion.V1_13_2_orHigher;
        } catch (ClassNotFoundException e) {
            return ServerVersion.V1_13_1_orLower;
        }

    }

    public void reloadConfigs() {

        reloadConfig();
        config = getConfig();
        loadConfigs();

    }

    private void loadConfigs() {

        loadLanguageConfig();
        ConfigUtils.setConfigs(config, messages);

    }

    private void loadIntegrations() {

        if (getServer().getPluginManager().getPlugin("PlaceholderAPI") != null) {
            isPlaceholderAPILoaded = true;
            logger.info("PlaceholderAPI detected, placeholder support enabled!");
        } else
            isPlaceholderAPILoaded = false;

    }

    private void registerBasicCommand() {

        PluginCommand command = getCommand("ultimateCheatCheck");
        UCCCommand uccCommand = new UCCCommand();
        command.setExecutor(uccCommand);
        command.setTabCompleter(uccCommand);

    }

    private void loadLanguageConfig() {

        final String langPath = "lang/messages_" + getConfig().getString("language") + ".yml";
        File langFile = new File(getDataFolder(), langPath);

        if (!langFile.exists()) {

            logger.info("§6The localization file specified in the config was not found!");
            logger.info("§6The standard localization file will be used ...");

            if (!new File(getDataFolder(), "lang/messages_en.yml").exists())
                saveResource("lang/messages_en.yml", false);
            if (!new File(getDataFolder(), "lang/messages_ru.yml").exists())
                saveResource("lang/messages_ru.yml", false);
            if (!new File(getDataFolder(), "lang/messages_ua.yml").exists())
                saveResource("lang/messages_ua.yml", false);
            if (!new File(getDataFolder(), "lang/messages_hu.yml").exists())
                saveResource("lang/messages_hu.yml", false);

        }

        messages = YamlConfiguration.loadConfiguration(new File(getDataFolder(), langPath));

    }

    public static UltimateCheatCheck getInstance() {
        return instance;
    }

    public boolean isPlaceholderAPILoaded() {
        return isPlaceholderAPILoaded;
    }

    public ServerVersion getServerVersion() {
        return serverVersion;
    }

    public RecoveryController getRecoveryController() {
        return recoveryController;
    }

    public CheckListeners getCheckListeners() {
        return checkListeners;
    }

}