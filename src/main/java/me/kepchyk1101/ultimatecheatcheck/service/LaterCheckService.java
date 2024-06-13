package me.kepchyk1101.ultimatecheatcheck.service;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import me.kepchyk1101.ultimatecheatcheck.util.ChatUtils;
import me.kepchyk1101.ultimatecheatcheck.util.ConfigUtils;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.jetbrains.annotations.NotNull;

import java.util.HashMap;
import java.util.Map;

@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class LaterCheckService implements Listener {

    @NotNull CheckService checkService;
    @NotNull Map<OfflinePlayer, Player> queueBySuspect = new HashMap<>();
    @NotNull Map<Player, OfflinePlayer> queueByInspector = new HashMap<>();

    public void startLater(@NotNull OfflinePlayer suspect, @NotNull Player inspector) {

        if (queueBySuspect.containsKey(suspect)) {
            ChatUtils.sendMessage(inspector, ConfigUtils.getMessage("cheatCheck.messagesToModer.you-already-wait-suspect")
                    .replace("%queue-suspect%", queueByInspector.get(inspector).getName()));
            return;
        }

        if (suspect.isOnline()) {
            checkService.start(suspect.getPlayer(), inspector, true);
            return;
        }

        queueBySuspect.put(suspect, inspector);
        queueByInspector.put(inspector, suspect);

        ChatUtils.sendMessage(inspector, ConfigUtils.getMessage("cheatCheck.messagesToModer.suspect-added-to-queue"));

    }

    @EventHandler
    private void onPlayerJoin(PlayerJoinEvent event) {

        Player player = event.getPlayer();
        if (!queueBySuspect.containsKey(player)) return;

        Player inspector = queueBySuspect.get(player);
        queueBySuspect.remove(player);
        queueByInspector.remove(inspector);

        checkService.start(player, inspector, true);

    }

    @EventHandler
    private void onPlayerQuit(PlayerQuitEvent event) {

        Player player = event.getPlayer();
        if (!queueByInspector.containsKey(player)) return;

        OfflinePlayer suspect = queueByInspector.get(player);
        queueByInspector.remove(player);
        queueBySuspect.remove(suspect);

    }

}
