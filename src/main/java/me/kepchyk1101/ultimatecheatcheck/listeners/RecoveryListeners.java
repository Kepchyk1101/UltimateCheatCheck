package me.kepchyk1101.ultimatecheatcheck.listeners;

import me.kepchyk1101.ultimatecheatcheck.UltimateCheatCheck;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.HandlerList;
import org.bukkit.event.Listener;
import org.bukkit.potion.PotionEffect;

import java.util.List;

// Я даже боюсь прикасаться к этому
public class RecoveryListeners implements Listener {

    @EventHandler
    private void onPlayerJoin(org.bukkit.event.player.PlayerJoinEvent event) {

        Player player = event.getPlayer();
        List<String> crashedPlayers = UltimateCheatCheck.getCrashedPlayers();

        if (crashedPlayers.contains(player.getName())) {

            for (PotionEffect effect : player.getActivePotionEffects())
                player.removePotionEffect(effect.getType());

            player.resetTitle();

            UltimateCheatCheck.getCrashedPlayers().remove(player.getName());

            if (crashedPlayers.size() == 0)
                HandlerList.unregisterAll(this);

        }

    }

}