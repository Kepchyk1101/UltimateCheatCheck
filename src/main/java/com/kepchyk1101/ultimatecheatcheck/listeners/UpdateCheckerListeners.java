package com.kepchyk1101.ultimatecheatcheck.listeners;

import com.kepchyk1101.ultimatecheatcheck.utils.ChatUtils;
import com.kepchyk1101.ultimatecheatcheck.utils.ConfigUtils;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;

public class UpdateCheckerListeners implements Listener {

    private final String latestVersion;

    public UpdateCheckerListeners(String latestVersion) {
        this.latestVersion = latestVersion;
    }

    @EventHandler
    private void onPlayerJoin(PlayerJoinEvent event) {

        final Player player = event.getPlayer();
        if (ConfigUtils.getBoolean("checkUpdates") && player.isOp()) {
            ChatUtils.sendMessage(player, ConfigUtils.getMessage("misc.availableNewPluginVersion")
                    .replace("%latestVersion%", this.latestVersion));
        }

    }

}