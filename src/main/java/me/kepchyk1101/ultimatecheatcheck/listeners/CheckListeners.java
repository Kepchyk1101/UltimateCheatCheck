package me.kepchyk1101.ultimatecheatcheck.listeners;

import me.kepchyk1101.ultimatecheatcheck.managers.CheatCheckManager;
import me.kepchyk1101.ultimatecheatcheck.util.ChatUtils;
import me.kepchyk1101.ultimatecheatcheck.util.ConfigUtils;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityPickupItemEvent;
import org.bukkit.event.player.*;

public class CheckListeners implements Listener {

    private final CheatCheckManager cheatCheckManager = CheatCheckManager.getInstance();

    @EventHandler(priority = EventPriority.MONITOR)
    public void onSuspectQuit(PlayerQuitEvent event) {

        final Player player = event.getPlayer();
        if (cheatCheckManager.isChecking(player)) {
            cheatCheckManager.suspectQuit(player);
        }

    }

    @EventHandler(priority = EventPriority.MONITOR)
    public void onModerQuit(PlayerQuitEvent event) {

        final Player player = event.getPlayer();
        if (cheatCheckManager.isModer(player)) {
            cheatCheckManager.moderQuit(player);
        }

    }

    @EventHandler
    public void onSuspectMove(PlayerMoveEvent event) {

        if (ConfigUtils.getBoolean("PlayerLocks.Moving.Disabled") &&
                cheatCheckManager.isChecking(event.getPlayer())) {
            event.setCancelled(true);
        }

    }

    @EventHandler
    public void onSuspectInteract(PlayerInteractEvent event) {

        if (ConfigUtils.getBoolean("PlayerLocks.Interact.Disabled") &&
                cheatCheckManager.isChecking(event.getPlayer())) {
            event.setCancelled(true);
        }

    }

    @EventHandler
    public void onEntityDamageSuspect(EntityDamageByEntityEvent event) {

        final Entity damaged = event.getEntity();
        final Entity damager = event.getDamager();

        if (damaged instanceof Player && cheatCheckManager.isChecking((Player) damaged)) {

            event.setCancelled(true);

            if (damager instanceof Player) {
                ChatUtils.sendMessage(damager, ConfigUtils.getMessage("errors.youCannotInteractWithSuspect"));
            }

        }

    }

    @EventHandler
    public void onSuspectDamageEntity(EntityDamageByEntityEvent event) {

        final Entity damager = event.getDamager();
        if (damager instanceof Player && cheatCheckManager.isChecking((Player) damager)) {
            event.setCancelled(true);
        }

    }

    @EventHandler
    public void onSuspectCommandPreprocess(PlayerCommandPreprocessEvent event) {

        final Player player = event.getPlayer();

        if (ConfigUtils.getBoolean("PlayerLocks.SendCommands.Disabled") &&
                cheatCheckManager.isChecking(player)) {

            for (String availableCommand : ConfigUtils.getStrings("PlayerLocks.SendCommands.AvailableCommands")) {
                if (event.getMessage().startsWith(availableCommand)) {
                    return;
                }
            }

            event.setCancelled(true);
            ChatUtils.sendMessage(player, ConfigUtils.getMessage("errors.youCannotUseCommands"));

        }

    }

    @EventHandler
    public void onSuspectChat(AsyncPlayerChatEvent event) {

        if (ConfigUtils.getBoolean("PlayerLocks.SendMessages.Disabled") &&
                cheatCheckManager.isChecking(event.getPlayer())) {
            event.setCancelled(true);
            ChatUtils.sendMessage(event.getPlayer(), ConfigUtils.getMessage("errors.youCannotUseChat"));
        }

    }

    @EventHandler
    public void onSuspectDropItem(PlayerDropItemEvent event) {

        if (ConfigUtils.getBoolean("PlayerLocks.DropItems.Disabled") &&
                cheatCheckManager.isChecking(event.getPlayer())) {
            event.setCancelled(true);
        }

    }

    @EventHandler
    public void onSuspectPickupItem(EntityPickupItemEvent event) {

        if (ConfigUtils.getBoolean("PlayerLocks.PickupItems.Disabled") &&
                event.getEntity() instanceof Player &&
                cheatCheckManager.isChecking((Player) event.getEntity())) {
            event.setCancelled(true);
        }

    }

}