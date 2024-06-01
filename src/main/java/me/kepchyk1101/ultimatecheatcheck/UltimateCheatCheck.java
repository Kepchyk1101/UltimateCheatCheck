package me.kepchyk1101.ultimatecheatcheck;

import lombok.Getter;
import me.kepchyk1101.ultimatecheatcheck.command.UCCCommand;
import me.kepchyk1101.ultimatecheatcheck.config.Localization;
import me.kepchyk1101.ultimatecheatcheck.listeners.CheckListeners;
import me.kepchyk1101.ultimatecheatcheck.service.CheckService;
import me.kepchyk1101.ultimatecheatcheck.util.ConfigUtils;
import me.kepchyk1101.ultimatecheatcheck.util.RecoveryController;
import me.kepchyk1101.ultimatecheatcheck.util.ServerVersion;
import me.kepchyk1101.ultimatecheatcheck.util.UpdateChecker;
import net.kyori.adventure.platform.bukkit.BukkitAudiences;
import org.bstats.bukkit.Metrics;
import org.bukkit.command.PluginCommand;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.event.HandlerList;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;
import java.util.logging.Logger;

public final class UltimateCheatCheck extends JavaPlugin {

    @Getter private static UltimateCheatCheck instance;
    @Getter private BukkitAudiences audiences;
    @Getter private boolean placeholderAPICompatibility;
    @Getter private ServerVersion serverVersion;
    @Getter private CheckListeners checkListeners;
    @Getter private RecoveryController recoveryController;
    private Logger logger;
    @Getter private Localization localization;
    private FileConfiguration config;

    private CheckService checkService;

    @Override
    public void onEnable() {

        instance = this;

        audiences = BukkitAudiences.create(this);

        checkService = new CheckService(this);

        logger = getLogger();

        serverVersion = checkServerVersion();

        checkListeners = new CheckListeners(checkService);

        localization = new Localization(this);

        // Configuration routine
        saveDefaultConfig();
        config = getConfig();
        loadConfigs();

        // If the server was shut down incorrectly, restore all damage
        recoveryAfterShutdownCheck();

        // Check for integrations on the server
        checkIntegrationsCompatibility();

        // Register basic command and subcommands
        if (!registerBasicCommand()) {
            logger.warning("Failed to register plugin command. Plugin shutdown...");
            getServer().getPluginManager().disablePlugin(this);
            return;
        }

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
        checkService.completionAllChecks();
        HandlerList.unregisterAll(checkListeners);

        // Clear recovery file
        FileConfiguration recoveryConfig = recoveryController.getConfig();
        for (String key : recoveryConfig.getConfigurationSection("checks").getKeys(false)) {
            recoveryConfig.set("checks." + key, null);
        }
        recoveryController.saveConfig();

        if (audiences != null) {
            audiences.close();
            audiences = null;
        }

        instance = null;

        logger.info("Plugin disabled!");

    }

    private void recoveryAfterShutdownCheck() {

        recoveryController = new RecoveryController(this, new File(getDataFolder(), "recovery.yml"));
        if (recoveryController.isNeedRecovery()) {
            recoveryController.startRecovery();
        }

    }

    private ServerVersion checkServerVersion() {

        final String version = getServer().getClass().getPackage().getName().split("\\.")[3];
        final String subVersion = version.replace("v1_", "").replaceAll("_R\\d", "");

        return (Integer.parseInt(subVersion) >= 16) ? ServerVersion.V1_16_orHigher : ServerVersion.V1_15_2_orLower;

    }

    public void reloadConfigs() {
        reloadConfig();
        config = getConfig();
        loadConfigs();
    }

    private void loadConfigs() {
        ConfigUtils.setConfigs(this.config, localization.loadLocalizedMessages());
    }

    private void checkIntegrationsCompatibility() {
        this.placeholderAPICompatibility = getServer().getPluginManager().isPluginEnabled("PlaceholderAPI");
        if (this.placeholderAPICompatibility) {
            logger.info("PlaceholderAPI detected, placeholder support enabled!");
        }
    }

    private boolean registerBasicCommand() {

        PluginCommand command = getCommand("ultimateCheatCheck");
        if (command != null) {
            UCCCommand uccCommand = new UCCCommand(checkService);
            command.setExecutor(uccCommand);
            command.setTabCompleter(uccCommand);
            return true;
        }

        return false;

    }

}