package me.kepchyk1101.ultimatecheatcheck.util;

import me.kepchyk1101.ultimatecheatcheck.UltimateCheatCheck;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.Scanner;
import java.util.logging.Logger;

public class UpdateChecker implements Listener {

    private final UltimateCheatCheck plugin;
    private final Logger logger;
    private final int resourceId;
    private String actualVersion;

    public UpdateChecker(UltimateCheatCheck plugin, int resourceId) {
        this.plugin = plugin;
        this.logger = this.plugin.getLogger();
        this.resourceId = resourceId;
    }

    public void checkPluginUpdates() {

        Bukkit.getScheduler().runTaskAsynchronously(plugin, () -> {

            try (InputStream is = new URL("https://api.spigotmc.org/legacy/update.php?resource=" + resourceId).openStream();
                 Scanner scan = new Scanner(is)) {

                if (scan.hasNext()) {

                    actualVersion = scan.next();

                    if (!plugin.getDescription().getVersion().equalsIgnoreCase(actualVersion)) {

                        logger.info("§6A new version of the plugin is available: " + actualVersion);
                        logger.info("§6You can download it here: https://www.spigotmc.org/resources/ultimatecheatcheck-1-12-2-1-20-1.112711/");
                        Bukkit.getPluginManager().registerEvents(this,plugin);

                    }

                }

            } catch (IOException e) {
                logger.warning("Failed to check for a new version of the plugin!");
            }

        });

    }

    @EventHandler(priority = EventPriority.MONITOR)
    private void onPlayerJoin(PlayerJoinEvent event) {

        final Player player = event.getPlayer();
        if (ConfigUtils.getBoolean("checkUpdates") && player.isOp()) {
            ChatUtils.sendMessage(player, ConfigUtils.getMessage("misc.availableNewPluginVersion")
                    .replace("%actualVersion%", actualVersion));
        }

    }

}