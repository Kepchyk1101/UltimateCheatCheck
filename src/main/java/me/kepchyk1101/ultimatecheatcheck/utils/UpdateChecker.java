package me.kepchyk1101.ultimatecheatcheck.utils;

import me.kepchyk1101.ultimatecheatcheck.UltimateCheatCheck;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.Scanner;

public class UpdateChecker implements Listener {

    private final UltimateCheatCheck plugin;
    private final int resourceId;
    private String actualVersion;

    public UpdateChecker(UltimateCheatCheck plugin, int resourceId) {
        this.plugin = plugin;
        this.resourceId = resourceId;
    }

    public void checkPluginUpdates() {

        Bukkit.getScheduler().runTaskAsynchronously(this.plugin, () -> {

            try (InputStream is = new URL("https://api.spigotmc.org/legacy/update.php?resource=" + this.resourceId).openStream();
                 Scanner scan = new Scanner(is)) {

                if (scan.hasNext()) {

                    this.actualVersion = scan.next();

                    if (!this.plugin.getDescription().getVersion().equalsIgnoreCase(this.actualVersion)) {

                        this.plugin.getLogger().info("§6A new version of the plugin is available: " + this.actualVersion);
                        this.plugin.getLogger().info("§6You can download it here: https://www.spigotmc.org/resources/ultimatecheatcheck-1-12-2-1-20-1.112711/");
                        Bukkit.getPluginManager().registerEvents(this, this.plugin);

                    }

                }

            } catch (IOException e) {
                this.plugin.getLogger().warning("Failed to check for a new version of the plugin!");
            }

        });

    }

    @EventHandler
    private void onPlayerJoin(PlayerJoinEvent event) {

        final Player player = event.getPlayer();
        if (ConfigUtils.getBoolean("checkUpdates") && player.isOp()) {
            ChatUtils.sendMessage(player, ConfigUtils.getMessage("misc.availableNewPluginVersion")
                    .replace("%actualVersion%", this.actualVersion));
        }

    }

}