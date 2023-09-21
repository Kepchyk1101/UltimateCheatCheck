package me.kepchyk1101.ultimatecheatcheck.utils;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.OfflinePlayer;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.HandlerList;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.potion.PotionEffect;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class RecoveryController implements Listener {

    private final JavaPlugin plugin;
    private final List<OfflinePlayer> crashedPlayers;
    private final File recoveryFile;
    private final FileConfiguration config;

    public RecoveryController(JavaPlugin plugin, File recoveryFile) {

        this.plugin = plugin;
        crashedPlayers = new ArrayList<>();
        this.recoveryFile = recoveryFile;
        if (!recoveryFile.exists()) {
            plugin.saveResource("recovery.yml", false);
        }
        config = YamlConfiguration.loadConfiguration(recoveryFile);

    }

    public boolean isNeedRecovery() {

        return config.getConfigurationSection("checks").getKeys(false).size() > 0;

    }

    public void startRecovery() {

        plugin.getLogger().info("§6Restoring a plugin after an emergency termination of the process ...");

        for (String key : config.getConfigurationSection("checks").getKeys(false)) {

            OfflinePlayer player = Bukkit.getOfflinePlayer(UUID.fromString(config.getString("checks." + key + ".suspect")));
            crashedPlayers.add(player);

            Bukkit.getWorld(config.getString("checks." + key + ".block.world")).getBlockAt(
                    config.getInt("checks." + key + ".block.x"),
                    config.getInt("checks." + key + ".block.y"),
                    config.getInt("checks." + key + ".block.z")
            ).setType(Material.valueOf(config.getString("checks." + key + ".block.type")));

            config.set("checks." + key, null);

            saveConfig();

        }


        Bukkit.getPluginManager().registerEvents(this, plugin);

    }

    public void saveConfig() {
        try {
            config.save(recoveryFile);
        } catch (IOException ignored) {
            // Unreal situation (i think..)
        }
    }

    @EventHandler
    private void onPlayerJoin(PlayerJoinEvent event) {

        final Player player = event.getPlayer();

        if (crashedPlayers.contains(player)) {

            for (PotionEffect effect : player.getActivePotionEffects()) {
                player.removePotionEffect(effect.getType());
            }

            player.resetTitle();

            crashedPlayers.remove(player);

            if (crashedPlayers.size() == 0) {
                HandlerList.unregisterAll(this);
            }

        }

    }

    public FileConfiguration getConfig() {
        return config;
    }

}
