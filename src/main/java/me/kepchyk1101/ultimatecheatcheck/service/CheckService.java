package me.kepchyk1101.ultimatecheatcheck.service;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import me.kepchyk1101.ultimatecheatcheck.UltimateCheatCheck;
import me.kepchyk1101.ultimatecheatcheck.cheatcheck.CheatCheck;
import me.kepchyk1101.ultimatecheatcheck.events.*;
import me.kepchyk1101.ultimatecheatcheck.util.ChatUtils;
import me.kepchyk1101.ultimatecheatcheck.util.ConfigUtils;
import me.kepchyk1101.ultimatecheatcheck.util.EventUtils;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.HandlerList;
import org.jetbrains.annotations.NotNull;

import java.util.HashMap;
import java.util.Map;

@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class CheckService {

    @NotNull UltimateCheatCheck plugin;
    @NotNull Map<Player, CheatCheck> activeChecksBySuspects = new HashMap<>();

    public void callPlayer(@NotNull Player suspect, @NotNull Player moderator) {

        if (!activeChecksBySuspects.containsKey(suspect)) {

            if (!suspect.hasPermission("ucc.immunity")) {

                CheatCheck cheatCheck = new CheatCheck(suspect, moderator, this);

                if (EventUtils.callAndCheckEvent(new CheatCheckStartEvent(cheatCheck))) return;

                cheatCheck.start();
                activeChecksBySuspects.put(suspect, cheatCheck);

                for (String message : ConfigUtils.getMessages("cheatCheck.messagesToSuspect.youCalledForCheck"))
                    ChatUtils.sendMessage(suspect, message
                            .replace("%time%", String.valueOf(cheatCheck.getTimer()))
                            .replace("%moder%", cheatCheck.getModerator().getName()));
                ChatUtils.sendMessage(moderator, ConfigUtils.getMessage("cheatCheck.messagesToModer.youStartedChecking")
                        .replace("%suspect%", suspect.getName()));

                cheatCheck.playSoundForSuspect("Sounds.OnCheatCheckStarted");

                if (activeChecksBySuspects.size() == 1) {
                    Bukkit.getPluginManager().registerEvents(plugin.getCheckListeners(), plugin);
                }

                ConfigUtils.getStrings("OnCheckStart.Commands").forEach(cmd -> {
                    Bukkit.dispatchCommand(Bukkit.getConsoleSender(), cmd
                            .replace("%suspect%", suspect.getName())
                            .replace("%inspector%", moderator.getName()));
                });

            } else
                ChatUtils.sendMessage(moderator, ConfigUtils.getMessage("errors.suspectHasImmunity"));

        } else
            ChatUtils.sendMessage(moderator, ConfigUtils.getMessage("errors.suspectAlreadyOnCheatCheck"));

    }

    // Признать игрока невиновным
    public void acquitPlayer(Player suspect, Player moderator) {

        if (activeChecksBySuspects.containsKey(suspect)) {

            CheatCheck cheatCheck = activeChecksBySuspects.get(suspect);

            if (EventUtils.callAndCheckEvent(new CheatCheckAcquitEvent(cheatCheck))) return;

            cheatCheck.stop();
            activeChecksBySuspects.remove(suspect);

            ChatUtils.sendMessage(suspect, ConfigUtils.getMessage("cheatCheck.messagesToSuspect.youAcquitted")
                    .replace("%moder%", moderator.getName()));
            ChatUtils.sendMessage(moderator, ConfigUtils.getMessage("cheatCheck.messagesToModer.youAcquittedSuspect")
                    .replace("%suspect%", suspect.getName()));

            cheatCheck.playSoundForSuspect("Sounds.OnSuspectAcquitted");

            if (activeChecksBySuspects.isEmpty()) {
                HandlerList.unregisterAll(plugin.getCheckListeners());
            }

        } else
            ChatUtils.sendMessage(moderator, ConfigUtils.getMessage("errors.cannotStopNotStartedCheatCheck"));

    }

    // Признать игрока виновным
    public void condemnPlayer(Player suspect, Player moderator) {

        if (activeChecksBySuspects.containsKey(suspect)) {

            CheatCheck cheatCheck = activeChecksBySuspects.get(suspect);

            if (EventUtils.callAndCheckEvent(new CheatCheckCondemnEvent(cheatCheck))) return;

            cheatCheck.stop();
            activeChecksBySuspects.remove(suspect);

            for (String punish : ConfigUtils.getStrings("AutoPunishments.Commands.OnSuspectCondemned")) {
                punish = punish.replace("%suspect%", suspect.getName());
                punish = punish.replace("%moder%", moderator.getName());
                Bukkit.dispatchCommand(Bukkit.getConsoleSender(), ChatUtils.format(punish, suspect));
            }

            ChatUtils.sendMessage(moderator, ConfigUtils.getMessage("cheatCheck.messagesToModer.youCondemnedSuspect")
                    .replace("%suspect%", suspect.getName()));

            if (activeChecksBySuspects.isEmpty()) {
                HandlerList.unregisterAll(plugin.getCheckListeners());
            }

        } else
            ChatUtils.sendMessage(moderator, ConfigUtils.getMessage("errors.cannotStopNotStartedCheatCheck"));

    }

    // Приостановить таймер проверки
    public void suspendCheck(Player suspect, Player moderator) {

        if (activeChecksBySuspects.containsKey(suspect)) {

            CheatCheck cheatCheck = activeChecksBySuspects.get(suspect);
            if (!cheatCheck.isPaused()) {

                if (EventUtils.callAndCheckEvent(new CheatCheckPauseEvent(cheatCheck))) return;

                cheatCheck.pause();

                ChatUtils.sendMessage(suspect, ConfigUtils.getMessage("cheatCheck.messagesToSuspect.yourCheckPaused")
                        .replace("%moder%", moderator.getName()));
                ChatUtils.sendMessage(moderator, ConfigUtils.getMessage("cheatCheck.messagesToModer.youPausedChecking")
                        .replace("%suspect%", suspect.getName()));

            } else
                ChatUtils.sendMessage(moderator, ConfigUtils.getMessage("errors.cheatCheckAlreadyPaused"));

        } else
            ChatUtils.sendMessage(moderator, ConfigUtils.getMessage("errors.cannotPauseNotStartedCheatCheck"));

    }

    // Игрок признался самостоятельно
    public void suspectConfess(Player suspect) {

        if (activeChecksBySuspects.containsKey(suspect)) {

            CheatCheck cheatCheck = activeChecksBySuspects.get(suspect);

            if (EventUtils.callAndCheckEvent(new CheatCheckConfessEvent(cheatCheck))) return;

            cheatCheck.stop();
            activeChecksBySuspects.remove(suspect);

            for (String punish : ConfigUtils.getStrings("AutoPunishments.Commands.OnSuspectConfess")) {
                punish = punish.replace("%suspect%", suspect.getName());
                punish = punish.replace("%moder%", cheatCheck.getModerator().getName());
                Bukkit.dispatchCommand(Bukkit.getConsoleSender(), ChatUtils.format(punish, suspect));
            }

            ChatUtils.sendMessage(cheatCheck.getModerator(), ConfigUtils.getMessage("cheatCheck.messagesToModer.suspectConfessed"));

        } else
            ChatUtils.sendMessage(suspect, ConfigUtils.getMessage("errors.youCannotConfess"));

    }

    public void playerContact(Player suspect, String contacts) {

        if (activeChecksBySuspects.containsKey(suspect)) {

            CheatCheck cheatCheck = activeChecksBySuspects.get(suspect);
            Player moderator = cheatCheck.getModerator();

            if (EventUtils.callAndCheckEvent(new CheatCheckContactEvent(cheatCheck, contacts))) return;

            ChatUtils.sendMessage(suspect, ConfigUtils.getMessage("cheatCheck.messagesToSuspect.youSentContacts")
                    .replace("%moder%", moderator.getName())
                    .replace("%contacts%", contacts));
            ChatUtils.sendMessage(moderator, ConfigUtils.getMessage("cheatCheck.messagesToModer.youReceivedContacts")
                    .replace("%suspect%", suspect.getName())
                    .replace("%contacts%", contacts));

        } else
            ChatUtils.sendMessage(suspect, ConfigUtils.getMessage("errors.cannotSendMessageToModer"));

    }

    public void suspectQuit(Player suspect) {

        CheatCheck cheatCheck = activeChecksBySuspects.get(suspect);

        cheatCheck.stop();
        activeChecksBySuspects.remove(suspect);

        for (String punish : ConfigUtils.getStrings("AutoPunishments.Commands.OnSuspectQuit")) {
            punish = punish.replace("%suspect%", suspect.getName());
            punish = punish.replace("%moder%", cheatCheck.getModerator().getName());
            Bukkit.dispatchCommand(Bukkit.getConsoleSender(), ChatUtils.format(punish, suspect));
        }

        ChatUtils.sendMessage(cheatCheck.getModerator(), ConfigUtils.getMessage("cheatCheck.messagesToModer.suspectQuit")
                .replace("%suspect%", suspect.getName()));

    }

    public void moderQuit(Player moder) {

        CheatCheck cheatCheck = findByModer(moder);

        cheatCheck.stop();
        Player suspect = cheatCheck.getSuspect();
        activeChecksBySuspects.remove(suspect);

        ChatUtils.sendMessage(suspect, ConfigUtils.getMessage("cheatCheck.messagesToSuspect.moderQuit")
                .replace("%moder%", cheatCheck.getModerator().getName()));

    }

    public void timerExpired(Player suspect) {

        CheatCheck cheatCheck = activeChecksBySuspects.get(suspect);

        cheatCheck.timerExpired();
        activeChecksBySuspects.remove(suspect);

        for (String punish : ConfigUtils.getStrings("AutoPunishments.Commands.OnSuspect`sTimerExpired")) {
            punish = punish.replace("%suspect%", suspect.getName());
            punish = punish.replace("%moder%", cheatCheck.getModerator().getName());
            Bukkit.dispatchCommand(Bukkit.getConsoleSender(), ChatUtils.format(punish, suspect));
        }

        ChatUtils.sendMessage(cheatCheck.getModerator(), ConfigUtils.getMessage("cheatCheck.messagesToModer.suspectsTimerExpired"));

    }

    public void completionAllChecks() {

        if (!activeChecksBySuspects.isEmpty()) {

            Bukkit.getLogger().info("Completing all active checks ...");

            for (CheatCheck cheatCheck : activeChecksBySuspects.values()) {
                cheatCheck.stop();
            }

            activeChecksBySuspects.clear();

        }

    }

    public boolean isChecking(Player suspect) {
        return activeChecksBySuspects.containsKey(suspect);
    }

    public boolean isModer(Player player) {
        for (CheatCheck cheatCheck : activeChecksBySuspects.values())
            if (cheatCheck.getModerator() == player)
                return true;
        return false;
    }

    private CheatCheck findByModer(Player moder) {
        for (CheatCheck cheatCheck : activeChecksBySuspects.values()) {
            if (cheatCheck.getModerator() == moder) {
                return cheatCheck;
            }
        }
        return null;
    }

    public CheatCheck findBySuspect(Player suspect) {
        return activeChecksBySuspects.get(suspect);
    }

}