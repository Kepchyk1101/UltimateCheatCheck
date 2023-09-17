package com.kepchyk1101.ultimatecheatcheck.listeners;

import com.kepchyk1101.ultimatecheatcheck.UltimateCheatCheck;
import com.kepchyk1101.ultimatecheatcheck.cheatcheck.CheatCheckManager;
import com.kepchyk1101.ultimatecheatcheck.utils.ChatUtils;
import com.kepchyk1101.ultimatecheatcheck.utils.ConfigUtils;
import org.bukkit.Sound;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.player.*;

public class AllListeners implements Listener {

    @EventHandler
    private void onSuspectQuit(org.bukkit.event.player.PlayerQuitEvent event) {

        Player player = event.getPlayer();
        if (event.getReason() == org.bukkit.event.player.PlayerQuitEvent.QuitReason.DISCONNECTED &&
                CheatCheckManager.isChecking(player))

            CheatCheckManager.suspectQuit(player);

    }

    @EventHandler
    private void onModerQuit(org.bukkit.event.player.PlayerQuitEvent event) {

        Player player = event.getPlayer();
        if (event.getReason() == org.bukkit.event.player.PlayerQuitEvent.QuitReason.DISCONNECTED &&
                CheatCheckManager.isModer(player))

            CheatCheckManager.moderQuit(player);

    }

    @EventHandler
    private void onSuspectMove(PlayerMoveEvent event) {

        if (ConfigUtils.getBoolean("PlayerLocks.Moving.Disabled") &&
                CheatCheckManager.isChecking(event.getPlayer()))

            event.setCancelled(true);

    }

    @EventHandler
    private void onSuspectInteract(PlayerInteractEvent event) {

        if (ConfigUtils.getBoolean("PlayerLocks.Interact.Disabled") &&
                CheatCheckManager.isChecking(event.getPlayer()))

            event.setCancelled(true);

    }

    @EventHandler
    private void onEntityDamageSuspect(EntityDamageByEntityEvent event) {

        Entity damaged = event.getEntity();
        Entity damager = event.getDamager();

        if (damaged instanceof Player && CheatCheckManager.isChecking((Player) damaged)) {

            if (damager instanceof Player) {

                ChatUtils.sendMessage(damager, ConfigUtils.getMessage("errors.youCannotInteractWithSuspect"));
                try {
                    Sound sound = Sound.valueOf(ConfigUtils.getString("PlayerLocks.TakeDamage.Sound"));
                    ((Player) damager).playSound(damaged.getLocation(), sound, 1f, 1f);
                } catch (IllegalArgumentException e) {
                    UltimateCheatCheck.getLog().warning(ConfigUtils.getMessage("system.unknownSound"));
                }

            }

            event.setCancelled(true);

        }

    }

    @EventHandler
    private void onSuspectDamageEntity(EntityDamageByEntityEvent event) {

        Entity damager = event.getDamager();

        if (damager instanceof Player && CheatCheckManager.isChecking((Player) damager))
            event.setCancelled(true);

    }

    @EventHandler
    private void onSuspectCommandPreprocess(PlayerCommandPreprocessEvent event) {

        Player player = event.getPlayer();

        if (ConfigUtils.getBoolean("PlayerLocks.SendCommands.Disabled") &&
                CheatCheckManager.isChecking(player)) {

            for (String availableCommand : ConfigUtils.getStrings("PlayerLocks.SendCommands.AvailableCommands"))
                if (event.getMessage().startsWith(availableCommand))
                    return;

            event.setCancelled(true);
            ChatUtils.sendMessage(player, ConfigUtils.getMessage("errors.youCannotUseCommands"));

        }

    }

    @EventHandler
    private void onSuspectChat(AsyncPlayerChatEvent event) {

        if (ConfigUtils.getBoolean("PlayerLocks.SendMessages.Disabled") &&
                CheatCheckManager.isChecking(event.getPlayer())) {

            event.setCancelled(true);
            ChatUtils.sendMessage(event.getPlayer(), ConfigUtils.getMessage("errors.youCannotUseChat"));

        }

    }

    @EventHandler
    private void onSuspectDropItem(PlayerDropItemEvent event) {

        if (ConfigUtils.getBoolean("PlayerLocks.DropItems.Disabled") &&
                CheatCheckManager.isChecking(event.getPlayer()))

            event.setCancelled(true);

    }

    @EventHandler
    private void onSuspectPickupItem(PlayerPickupItemEvent event) {

        if (ConfigUtils.getBoolean("PlayerLocks.PickupItems.Disabled") &&
                CheatCheckManager.isChecking(event.getPlayer()))

            event.setCancelled(true);

    }

}