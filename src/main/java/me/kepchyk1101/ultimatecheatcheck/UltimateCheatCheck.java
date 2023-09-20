package me.kepchyk1101.ultimatecheatcheck;

import me.kepchyk1101.ultimatecheatcheck.cheatcheck.CheatCheckManager;
import me.kepchyk1101.ultimatecheatcheck.command.UCCCommand;
import me.kepchyk1101.ultimatecheatcheck.listeners.CheckListeners;
import me.kepchyk1101.ultimatecheatcheck.listeners.RecoveryListeners;
import me.kepchyk1101.ultimatecheatcheck.utils.Checks;
import me.kepchyk1101.ultimatecheatcheck.utils.ConfigUtils;
import me.kepchyk1101.ultimatecheatcheck.utils.ServerVersion;
import me.kepchyk1101.ultimatecheatcheck.utils.UpdateChecker;
import org.bstats.bukkit.Metrics;
import org.bukkit.Material;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.PluginCommand;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.event.HandlerList;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;

public final class UltimateCheatCheck extends JavaPlugin {

    private static UltimateCheatCheck instance;
    private Logger logger;
    private boolean isPlaceholderAPILoaded;
    private ServerVersion serverVersion;

    private FileConfiguration messages;
    private FileConfiguration config;
    
    private static Checks checks;
    private static FileConfiguration checksConfig;
    private static List<String> crashedPlayers;

    @Override
    public void onEnable() {

        instance = this;
        logger = getLogger();

        serverVersion = checkServerVersion();

        saveDefaultConfig();
        config = getConfig();
        loadConfigs();

        loadChecksFile();
        loadIntegrations();
        registerBasicCommand();

        if (ConfigUtils.getBoolean("checkUpdates")) {
            new UpdateChecker(this, 112711).checkPluginUpdates();
        }

        new Metrics(this, 19805);

        logger.info("Plugin launched successfully!");

    }

    @Override
    public void onDisable() {

        CheatCheckManager.completionAllChecks();
        HandlerList.unregisterAll(new CheckListeners());
        for (String key : checksConfig.getConfigurationSection("checks").getKeys(false))
            checksConfig.set("checks." + key, null);
        checks.save();

    }

    private ServerVersion checkServerVersion() {

        try {
            Class.forName("org.bukkit.util.BoundingBox");
            return serverVersion = ServerVersion.V1_13_2_orHigher;
        } catch (ClassNotFoundException e) {
            return serverVersion = ServerVersion.V1_13_1_orLower;
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

    private void loadChecksFile() {

        checks = new Checks(new File(getDataFolder(), "checks.yml"));
        checksConfig = checks.getConfig();
        if (!checks.isExists()) {
            return;
        }
        if (checksConfig.getConfigurationSection("checks").getKeys(false).size() >= 1)
            recoveryAfterCrash();

    }

    // К этому тоже стрёмно прикасаться
    private void recoveryAfterCrash() {

        logger.info("&6Restoring a plugin after an emergency termination of the process ...");

        crashedPlayers = new ArrayList<>();

        for (String key : checksConfig.getConfigurationSection("checks").getKeys(false)) {

            OfflinePlayer player = getServer().getOfflinePlayer(checksConfig.getString("checks." + key + ".suspect"));
            crashedPlayers.add(player.getName());

            getServer().getWorld(checksConfig.getString("checks." + key + ".block.world")).getBlockAt(
                    checksConfig.getInt("checks." + key + ".block.x"),
                    checksConfig.getInt("checks." + key + ".block.y"),
                    checksConfig.getInt("checks." + key + ".block.z")
            ).setType(Material.valueOf(checksConfig.getString("checks." + key + ".block.type")));

            checksConfig.set("checks." + key, null);
            checks.save();

        }

        getServer().getPluginManager().registerEvents(new RecoveryListeners(), this);

    }

    private void loadLanguageConfig() {

        File langFile = new File(getDataFolder(), "lang/messages_" + getConfig().getString("language") + ".yml");

        if (langFile.exists()) {
            messages = YamlConfiguration.loadConfiguration(langFile);
            return;
        }

        if (!new File(getDataFolder(), "lang/messages_ru.yml").exists())
            saveResource("lang/messages_ru.yml", false);
        if (!new File(getDataFolder(), "lang/messages_en.yml").exists())
            saveResource("lang/messages_en.yml", false);

        logger.info("§6The localization file specified in the config was not found! The standard localization file will be used ...");

        messages = YamlConfiguration.loadConfiguration(new File(getDataFolder(), "lang/messages_ru.yml"));

    }

    public static UltimateCheatCheck getInstance() {
        return instance;
    }

    public boolean isPlaceholderAPILoaded() {
        return isPlaceholderAPILoaded;
    }

    public static List<String> getCrashedPlayers() {
        return crashedPlayers;
    }

    public static Checks getChecks() {
        return checks;
    }

    public ServerVersion getServerVersion() {
        return serverVersion;
    }
}