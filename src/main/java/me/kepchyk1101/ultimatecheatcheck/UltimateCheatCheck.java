package me.kepchyk1101.ultimatecheatcheck;

import lombok.Getter;
import me.kepchyk1101.ultimatecheatcheck.command.ConfessCommand;
import me.kepchyk1101.ultimatecheatcheck.command.UCCCommand;
import me.kepchyk1101.ultimatecheatcheck.config.Localization;
import me.kepchyk1101.ultimatecheatcheck.listeners.CheckListeners;
import me.kepchyk1101.ultimatecheatcheck.service.CheckService;
import me.kepchyk1101.ultimatecheatcheck.util.ConfigUtils;
import me.kepchyk1101.ultimatecheatcheck.util.RecoveryController;
import me.kepchyk1101.ultimatecheatcheck.util.UpdateChecker;
import net.kyori.adventure.platform.bukkit.BukkitAudiences;
import org.bstats.bukkit.Metrics;
import org.bukkit.Bukkit;
import org.bukkit.command.PluginCommand;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.event.HandlerList;
import org.bukkit.plugin.java.JavaPlugin;
import org.jetbrains.annotations.Nullable;
import ru.leymooo.antirelog.Antirelog;
import ru.leymooo.antirelog.manager.PvPManager;

import java.io.File;
import java.util.logging.Logger;

public final class UltimateCheatCheck extends JavaPlugin {

    @Getter private static UltimateCheatCheck instance;
    @Getter private BukkitAudiences audiences;
    @Getter private boolean placeholderAPICompatibility;
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

        checkService = new CheckService(getPvPManagerInstance());

        logger = getLogger();

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

        getCommand("ultimateCheatCheck").setExecutor(new UCCCommand(checkService));
        getCommand("confess").setExecutor(new ConfessCommand(checkService));

        getServer().getPluginManager().registerEvents(checkListeners, this);

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
        Bukkit.getLogger().info("Completing all active checks ...");
        checkService.stopAllChecks();
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

    @Nullable
    private Antirelog getAntiRelogInstance() {
        return (Antirelog) Bukkit.getPluginManager().getPlugin("AntiRelog");
    }

    @Nullable
    private PvPManager getPvPManagerInstance() {
        Antirelog antirelog = getAntiRelogInstance();
        return antirelog != null ? antirelog.getPvpManager() : null;
    }

}