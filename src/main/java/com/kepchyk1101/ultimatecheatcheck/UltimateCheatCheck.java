package com.kepchyk1101.ultimatecheatcheck;

import com.kepchyk1101.ultimatecheatcheck.cheatcheck.CheatCheckManager;
import com.kepchyk1101.ultimatecheatcheck.command.UCCExecutor;
import com.kepchyk1101.ultimatecheatcheck.command.UCCTab;
import com.kepchyk1101.ultimatecheatcheck.listeners.AllListeners;
import com.kepchyk1101.ultimatecheatcheck.listeners.RecoveryListeners;
import com.kepchyk1101.ultimatecheatcheck.listeners.UpdateCheckerListeners;
import com.kepchyk1101.ultimatecheatcheck.utils.Checks;
import com.kepchyk1101.ultimatecheatcheck.utils.ConfigUtils;
import com.kepchyk1101.ultimatecheatcheck.utils.UpdateChecker;
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
    private static Logger log;
    private static boolean isPlaceholderAPILoaded;

    private FileConfiguration messages;
    private FileConfiguration config;

    // Необходимое для восстановления, в случае экстренного выключения сервера
    private static Checks checks;
    private static FileConfiguration checksConfig;
    private static List<String> crashedPlayers; // Ничего умнее чем засунуть стринговые ники игроков сюда - я не придумал. Оно все не работало :<

    @Override
    public void onEnable() {

        instance = this;
        log = getLogger();

        saveDefaultConfig();
        config = getConfig();
        loadConfigs();

        loadChecksFile();
        loadIntegrations();
        loadCommand();

        //checkUpdates();

        new Metrics(this, 19805);

        log.info(ConfigUtils.getMessage("system.pluginEnabled"));

    }

    @Override
    public void onDisable() {

        CheatCheckManager.completionAllChecks();
        HandlerList.unregisterAll(new AllListeners());
        for (String key : checksConfig.getConfigurationSection("checks").getKeys(false))
            checksConfig.set("checks." + key, null);
        checks.save();

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
            log.info(ConfigUtils.getMessage("system.enabledPlaceholderAPI"));
        } else
            isPlaceholderAPILoaded = false;

    }

    private void loadCommand() {

        PluginCommand command = getCommand("ultimateCheatCheck");
        command.setExecutor(new UCCExecutor());
        command.setTabCompleter(new UCCTab());

    }

    private void checkUpdates() {

        new UpdateChecker(this, 1337).getLatestVersion(version -> {
            log.warning(ConfigUtils.getMessage("system.availableNewPluginVersion")
                    .replace("%latestVersion%", version));
            getServer().getPluginManager().registerEvents(new UpdateCheckerListeners(version), instance);
        });

    }

    private void loadChecksFile() {

        checks = new Checks(new File(getDataFolder(), "checks.yml"));
        checksConfig = checks.getConfig();
        if (!checks.isExists()) return;
        if (checksConfig.getConfigurationSection("checks").getKeys(false).size() >= 1)
            recoveryAfterCrash();

    }

    // К этому тоже стрёмно прикасаться
    private void recoveryAfterCrash() {

        log.info(ConfigUtils.getMessage("system.pluginRecovery"));

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

        try {
            log.info(ConfigUtils.getMessage("system.languageFileNotFound"));
        } catch (Exception e) {
            log.info("Файл локализации указанный в конфиге не найден! Будет использоваться стандартный файл локализации ...");
        }
        messages = YamlConfiguration.loadConfiguration(new File(getDataFolder(), "lang/messages_ru.yml"));

    }

    public static UltimateCheatCheck getInstance() {
        return instance;
    }

    public static boolean isPlaceholderAPILoaded() {
        return isPlaceholderAPILoaded;
    }

    public static Logger getLog() {
        return log;
    }

    public static List<String> getCrashedPlayers() {
        return crashedPlayers;
    }

    public static Checks getChecks() {
        return checks;
    }

}