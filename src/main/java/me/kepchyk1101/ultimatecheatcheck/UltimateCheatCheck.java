package me.kepchyk1101.ultimatecheatcheck;

import com.earth2me.essentials.Essentials;
import lombok.Getter;
import me.kepchyk1101.ultimatecheatcheck.command.ConfessCommand;
import me.kepchyk1101.ultimatecheatcheck.command.UCCCommand;
import me.kepchyk1101.ultimatecheatcheck.config.Localization;
import me.kepchyk1101.ultimatecheatcheck.listeners.CheckListeners;
import me.kepchyk1101.ultimatecheatcheck.service.CheckService;
import me.kepchyk1101.ultimatecheatcheck.service.LaterCheckService;
import me.kepchyk1101.ultimatecheatcheck.service.afk.AfkChecker;
import me.kepchyk1101.ultimatecheatcheck.service.afk.EssentialsAfkChecker;
import me.kepchyk1101.ultimatecheatcheck.util.ConfigUtils;
import me.kepchyk1101.ultimatecheatcheck.util.UpdateChecker;
import net.kyori.adventure.platform.bukkit.BukkitAudiences;
import org.bstats.bukkit.Metrics;
import org.bukkit.Bukkit;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.event.HandlerList;
import org.bukkit.plugin.java.JavaPlugin;
import org.jetbrains.annotations.Nullable;
import ru.leymooo.antirelog.Antirelog;
import ru.leymooo.antirelog.manager.PvPManager;

import java.util.logging.Logger;

public final class UltimateCheatCheck extends JavaPlugin {

    @Getter private static UltimateCheatCheck instance;
    @Getter private BukkitAudiences audiences;
    @Getter private boolean placeholderAPICompatibility;
    @Getter private CheckListeners checkListeners;
    private Logger logger;
    @Getter private Localization localization;
    private FileConfiguration config;

    private CheckService checkService;
    private LaterCheckService laterCheckService;

    @Override
    public void onEnable() {

        instance = this;

        audiences = BukkitAudiences.create(this);

        Essentials ess = (Essentials) getServer().getPluginManager().getPlugin("Essentials");
        AfkChecker afkChecker = null;
        if (ess != null && ess.isEnabled()) {
            afkChecker = new EssentialsAfkChecker(ess);
        }

        checkService = new CheckService(getPvPManagerInstance(), afkChecker);
        laterCheckService = new LaterCheckService(checkService);

        logger = getLogger();

        checkListeners = new CheckListeners(checkService);

        localization = new Localization(this);

        // Configuration routine
        saveDefaultConfig();
        config = getConfig();
        loadConfigs();

        // Check for integrations on the server
        checkIntegrationsCompatibility();

        getCommand("ultimateCheatCheck").setExecutor(new UCCCommand(checkService, laterCheckService));
        getCommand("confess").setExecutor(new ConfessCommand(checkService));

        getServer().getPluginManager().registerEvents(checkListeners, this);
        getServer().getPluginManager().registerEvents(laterCheckService, this);

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

        if (audiences != null) {
            audiences.close();
            audiences = null;
        }

        instance = null;

        logger.info("Plugin disabled!");

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