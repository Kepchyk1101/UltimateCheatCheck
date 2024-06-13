package me.kepchyk1101.ultimatecheatcheck.listeners;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import me.kepchyk1101.ultimatecheatcheck.UltimateCheatCheck;
import me.kepchyk1101.ultimatecheatcheck.service.CheckService;
import me.kepchyk1101.ultimatecheatcheck.util.ChatUtils;
import me.kepchyk1101.ultimatecheatcheck.util.ConfigUtils;
import org.bukkit.Bukkit;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityPickupItemEvent;
import org.bukkit.event.player.*;
import org.jetbrains.annotations.NotNull;

@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class CheckListeners implements Listener {

    @NotNull CheckService checkService;

    @EventHandler
    private void onSuspectQuit(PlayerQuitEvent event) {

        final Player player = event.getPlayer();
        if (checkService.isSuspect(player)) {
            checkService.suspectQuit(player);
        }

    }

    @EventHandler
    private void onModerQuit(PlayerQuitEvent event) {

        Player player = event.getPlayer();
        if (checkService.isInspector(player)) {
            checkService.moderQuit(player);
        }

    }

    @EventHandler
    private void onSuspectMove(PlayerMoveEvent event) {

        if (!checkService.isSuspect(event.getPlayer())) return;
        if (!ConfigUtils.getBoolean("PlayerLocks.Moving.Disabled")) return;

        event.setCancelled(true);

    }

    @EventHandler
    private void onSuspectInteract(PlayerInteractEvent event) {

        if (ConfigUtils.getBoolean("PlayerLocks.Interact.Disabled") &&
                checkService.isSuspect(event.getPlayer())) {
            event.setCancelled(true);
        }

    }

    @EventHandler
    private void onEntityDamageSuspect(EntityDamageByEntityEvent event) {

        final Entity damaged = event.getEntity();
        final Entity damager = event.getDamager();

        if (damaged instanceof Player && checkService.isSuspect((Player) damaged)) {

            event.setCancelled(true);

            if (damager instanceof Player) {
                ChatUtils.sendMessage(damager, ConfigUtils.getMessage("errors.youCannotInteractWithSuspect"));
            }

        }

    }

    @EventHandler
    private void onSuspectDamageEntity(EntityDamageByEntityEvent event) {

        final Entity damager = event.getDamager();
        if (damager instanceof Player && checkService.isSuspect((Player) damager)) {
            event.setCancelled(true);
        }

    }

    @EventHandler
    private void onSuspectCommandPreprocess(PlayerCommandPreprocessEvent event) {

        Player player = event.getPlayer();

        if (ConfigUtils.getBoolean("PlayerLocks.SendCommands.Disabled") &&
                checkService.isSuspect(player)) {

            if (event.getMessage().equals("/confess")) return;

            for (String availableCommand : ConfigUtils.getStrings("PlayerLocks.SendCommands.AvailableCommands")) {
                if (event.getMessage().startsWith(availableCommand)) {
                    return;
                }
            }

            event.setCancelled(true);
            ChatUtils.sendMessage(player, ConfigUtils.getMessage("errors.youCannotUseCommands"));

        }

    }

    @EventHandler(priority = EventPriority.LOWEST)
    private void onSuspectChat(AsyncPlayerChatEvent event) {

        Player player = event.getPlayer();
        if (!checkService.isSuspect(player)) return;
        //if (!ConfigUtils.getBoolean("PlayerLocks.SendMessages.Disabled")) return;

        event.setCancelled(true);

        Bukkit.getScheduler().runTask(
                UltimateCheatCheck.getInstance(),
                () -> checkService.playerContact(player, event.getMessage())
        );

    }

    @EventHandler
    private void onSuspectDropItem(PlayerDropItemEvent event) {

        if (ConfigUtils.getBoolean("PlayerLocks.DropItems.Disabled") &&
                checkService.isSuspect(event.getPlayer())) {
            event.setCancelled(true);
        }

    }

    @EventHandler
    private void onSuspectPickupItem(EntityPickupItemEvent event) {

        if (ConfigUtils.getBoolean("PlayerLocks.PickupItems.Disabled") &&
                event.getEntity() instanceof Player &&
                checkService.isSuspect((Player) event.getEntity())) {
            event.setCancelled(true);
        }

    }

}