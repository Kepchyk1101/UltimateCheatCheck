package me.kepchyk1101.ultimatecheatcheck.service;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import me.kepchyk1101.ultimatecheatcheck.cheatcheck.CheatCheck;
import me.kepchyk1101.ultimatecheatcheck.events.*;
import me.kepchyk1101.ultimatecheatcheck.service.afk.AfkChecker;
import me.kepchyk1101.ultimatecheatcheck.util.ChatUtils;
import me.kepchyk1101.ultimatecheatcheck.util.ConfigUtils;
import me.kepchyk1101.ultimatecheatcheck.util.EventUtils;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import ru.leymooo.antirelog.manager.PvPManager;

import java.util.HashMap;
import java.util.Map;

@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class CheckService {

    @NotNull Map<Player, CheatCheck> activeChecksBySuspects = new HashMap<>();
    @NotNull Map<Player, CheatCheck> activeChecksByInspectors = new HashMap<>();
    @Nullable PvPManager pvpManager; // AntiRelog
    @Nullable AfkChecker afkChecker; // EssentialsX /afk

    public void start(@NotNull Player suspect, @NotNull Player inspector, boolean force) {

        if (!activeChecksBySuspects.containsKey(suspect)) {

            if (!suspect.hasPermission("ucc.immunity")) {

                CheatCheck cheatCheck = new CheatCheck(suspect, inspector, this);

                if (EventUtils.callAndCheckEvent(new CheatCheckStartEvent(cheatCheck))) return;

                if (pvpManager != null && pvpManager.isInPvP(suspect)) {
                    pvpManager.stopPvPSilent(suspect);
                }

                if (!force && ConfigUtils.getBoolean("experimental.afk-check")
                        && afkChecker != null && afkChecker.isAfk(suspect)) {
                    ChatUtils.sendMessage(inspector, ConfigUtils.getMessage("errors.cant-call-suspect-afk"));
                    return;
                }

                cheatCheck.start();
                activeChecksBySuspects.put(suspect, cheatCheck);
                activeChecksByInspectors.put(inspector, cheatCheck);

                for (String message : ConfigUtils.getMessages("cheatCheck.messagesToSuspect.youCalledForCheck"))
                    ChatUtils.sendMessage(suspect, message
                            .replace("%time%", String.valueOf(cheatCheck.getTimer()))
                            .replace("%moder%", cheatCheck.getModerator().getName()));
                ChatUtils.sendMessage(inspector, ConfigUtils.getMessage("cheatCheck.messagesToModer.youStartedChecking")
                        .replace("%suspect%", suspect.getName()));

                cheatCheck.playSoundForSuspect("Sounds.OnCheatCheckStarted");

                ConfigUtils.getStrings("OnCheckStart.Commands").forEach(cmd -> {
                    Bukkit.dispatchCommand(Bukkit.getConsoleSender(), cmd
                            .replace("%suspect%", suspect.getName())
                            .replace("%inspector%", inspector.getName()));
                });

            } else
                ChatUtils.sendMessage(inspector, ConfigUtils.getMessage("errors.suspectHasImmunity"));

        } else
            ChatUtils.sendMessage(inspector, ConfigUtils.getMessage("errors.suspectAlreadyOnCheatCheck"));

    }

    public void acquitPlayer(@NotNull Player suspect, @NotNull Player inspector) {

        if (activeChecksBySuspects.containsKey(suspect)) {

            CheatCheck cheatCheck = activeChecksBySuspects.get(suspect);

            if (EventUtils.callAndCheckEvent(new CheatCheckAcquitEvent(cheatCheck))) return;

            cheatCheck.stop();
            activeChecksBySuspects.remove(suspect);
            activeChecksByInspectors.remove(inspector);

            ChatUtils.sendMessage(suspect, ConfigUtils.getMessage("cheatCheck.messagesToSuspect.youAcquitted")
                    .replace("%moder%", inspector.getName()));
            ChatUtils.sendMessage(inspector, ConfigUtils.getMessage("cheatCheck.messagesToModer.youAcquittedSuspect")
                    .replace("%suspect%", suspect.getName()));

            cheatCheck.playSoundForSuspect("Sounds.OnSuspectAcquitted");

        } else
            ChatUtils.sendMessage(inspector, ConfigUtils.getMessage("errors.cannotStopNotStartedCheatCheck"));

    }

    public void condemnPlayer(@NotNull Player suspect, @NotNull Player inspector) {

        if (activeChecksBySuspects.containsKey(suspect)) {

            CheatCheck cheatCheck = activeChecksBySuspects.get(suspect);

            if (EventUtils.callAndCheckEvent(new CheatCheckCondemnEvent(cheatCheck))) return;

            cheatCheck.stop();
            activeChecksBySuspects.remove(suspect);
            activeChecksByInspectors.remove(inspector);

            for (String punish : ConfigUtils.getStrings("AutoPunishments.Commands.OnSuspectCondemned")) {
                punish = punish.replace("%suspect%", suspect.getName());
                punish = punish.replace("%moder%", inspector.getName());
                Bukkit.dispatchCommand(Bukkit.getConsoleSender(), ChatUtils.format(punish, suspect));
            }

            ChatUtils.sendMessage(inspector, ConfigUtils.getMessage("cheatCheck.messagesToModer.youCondemnedSuspect")
                    .replace("%suspect%", suspect.getName()));

        } else
            ChatUtils.sendMessage(inspector, ConfigUtils.getMessage("errors.cannotStopNotStartedCheatCheck"));

    }

    public void suspendCheck(@NotNull Player suspect, @NotNull Player moderator) {

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

    public void suspectConfess(@NotNull Player suspect) {

        if (activeChecksBySuspects.containsKey(suspect)) {

            CheatCheck cheatCheck = activeChecksBySuspects.get(suspect);

            if (EventUtils.callAndCheckEvent(new CheatCheckConfessEvent(cheatCheck))) return;

            cheatCheck.stop();
            activeChecksBySuspects.remove(suspect);
            activeChecksByInspectors.remove(cheatCheck.getModerator());

            for (String punish : ConfigUtils.getStrings("AutoPunishments.Commands.OnSuspectConfess")) {
                punish = punish.replace("%suspect%", suspect.getName());
                punish = punish.replace("%moder%", cheatCheck.getModerator().getName());
                Bukkit.dispatchCommand(Bukkit.getConsoleSender(), ChatUtils.format(punish, suspect));
            }

            ChatUtils.sendMessage(cheatCheck.getModerator(), ConfigUtils.getMessage("cheatCheck.messagesToModer.suspectConfessed"));

        } else
            ChatUtils.sendMessage(suspect, ConfigUtils.getMessage("errors.youCannotConfess"));

    }

    public void playerContact(@NotNull Player suspect, @NotNull String contacts) {

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

    public void suspectQuit(@NotNull Player suspect) {

        CheatCheck cheatCheck = activeChecksBySuspects.get(suspect);

        cheatCheck.stop();
        activeChecksBySuspects.remove(suspect);
        activeChecksByInspectors.remove(cheatCheck.getModerator());

        for (String punish : ConfigUtils.getStrings("AutoPunishments.Commands.OnSuspectQuit")) {
            punish = punish.replace("%suspect%", suspect.getName());
            punish = punish.replace("%moder%", cheatCheck.getModerator().getName());
            Bukkit.dispatchCommand(Bukkit.getConsoleSender(), ChatUtils.format(punish, suspect));
        }

        ChatUtils.sendMessage(cheatCheck.getModerator(), ConfigUtils.getMessage("cheatCheck.messagesToModer.suspectQuit")
                .replace("%suspect%", suspect.getName()));

    }

    public void moderQuit(@NotNull Player moder) {

        CheatCheck cheatCheck = getByInspector(moder);

        cheatCheck.stop();
        Player suspect = cheatCheck.getSuspect();
        activeChecksBySuspects.remove(suspect);
        activeChecksByInspectors.remove(cheatCheck.getModerator());

        ChatUtils.sendMessage(suspect, ConfigUtils.getMessage("cheatCheck.messagesToSuspect.moderQuit")
                .replace("%moder%", cheatCheck.getModerator().getName()));

    }

    public void timerExpired(@NotNull Player suspect) {

        CheatCheck cheatCheck = activeChecksBySuspects.get(suspect);

        cheatCheck.timerExpired();
        activeChecksBySuspects.remove(suspect);
        activeChecksByInspectors.remove(cheatCheck.getModerator());

        for (String punish : ConfigUtils.getStrings("AutoPunishments.Commands.OnSuspect`sTimerExpired")) {
            punish = punish.replace("%suspect%", suspect.getName());
            punish = punish.replace("%moder%", cheatCheck.getModerator().getName());
            Bukkit.dispatchCommand(Bukkit.getConsoleSender(), ChatUtils.format(punish, suspect));
        }

        ChatUtils.sendMessage(cheatCheck.getModerator(), ConfigUtils.getMessage("cheatCheck.messagesToModer.suspectsTimerExpired"));

    }

    public void stopAllChecks() {

        activeChecksBySuspects.forEach((suspect, check) -> check.stop());
        activeChecksBySuspects.clear();
        activeChecksByInspectors.clear();

    }

    public boolean isSuspect(@NotNull Player player) {
        return activeChecksBySuspects.containsKey(player);
    }

    public boolean isInspector(@NotNull Player player) {
        return activeChecksByInspectors.containsKey(player);
    }

    @Nullable
    private CheatCheck getByInspector(@NotNull Player player) {
        return activeChecksByInspectors.get(player);
    }

    @Nullable
    public CheatCheck getBySuspect(@NotNull Player player) {
        return activeChecksBySuspects.get(player);
    }

}