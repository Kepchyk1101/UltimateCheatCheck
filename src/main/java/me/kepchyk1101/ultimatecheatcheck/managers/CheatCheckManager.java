package me.kepchyk1101.ultimatecheatcheck.managers;

import me.kepchyk1101.ultimatecheatcheck.UltimateCheatCheck;
import me.kepchyk1101.ultimatecheatcheck.cheatcheck.CheatCheck;
import me.kepchyk1101.ultimatecheatcheck.events.*;
import me.kepchyk1101.ultimatecheatcheck.util.ChatUtils;
import me.kepchyk1101.ultimatecheatcheck.util.ConfigUtils;
import me.kepchyk1101.ultimatecheatcheck.util.EventUtils;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.HandlerList;

import java.util.HashMap;
import java.util.Map;

public class CheatCheckManager {

    private static CheatCheckManager instance;
    private static final UltimateCheatCheck PLUGIN = UltimateCheatCheck.getInstance();
    private static final Map<Player, CheatCheck> ACTIVE_CHECKS = new HashMap<>();

    public void callPlayer(Player suspect, Player moderator) {

        if (!ACTIVE_CHECKS.containsKey(suspect)) {

            if (!suspect.hasPermission("ucc.immunity")) {

                CheatCheck cheatCheck = new CheatCheck(suspect, moderator);

                if (EventUtils.callAndCheckEvent(new CheatCheckStartEvent(cheatCheck))) return;

                cheatCheck.start();
                ACTIVE_CHECKS.put(suspect, cheatCheck);

                for (String message : ConfigUtils.getMessages("cheatCheck.messagesToSuspect.youCalledForCheck"))
                    ChatUtils.sendMessage(suspect, message
                            .replace("%time%", String.valueOf(cheatCheck.getTimer()))
                            .replace("%moder%", cheatCheck.getModerator().getName()));
                ChatUtils.sendMessage(moderator, ConfigUtils.getMessage("cheatCheck.messagesToModer.youStartedChecking")
                        .replace("%suspect%", suspect.getName()));

                cheatCheck.playSoundForSuspect("Sounds.OnCheatCheckStarted");

                if (ACTIVE_CHECKS.size() == 1) {
                    Bukkit.getPluginManager().registerEvents(PLUGIN.getCheckListeners(), PLUGIN);
                }

            } else
                ChatUtils.sendMessage(moderator, ConfigUtils.getMessage("errors.suspectHasImmunity"));

        } else
            ChatUtils.sendMessage(moderator, ConfigUtils.getMessage("errors.suspectAlreadyOnCheatCheck"));

    }

    // Признать игрока невиновным
    public void acquitPlayer(Player suspect, Player moderator) {

        if (ACTIVE_CHECKS.containsKey(suspect)) {

            CheatCheck cheatCheck = ACTIVE_CHECKS.get(suspect);

            if (EventUtils.callAndCheckEvent(new CheatCheckAcquitEvent(cheatCheck))) return;

            cheatCheck.stop();
            ACTIVE_CHECKS.remove(suspect);

            ChatUtils.sendMessage(suspect, ConfigUtils.getMessage("cheatCheck.messagesToSuspect.youAcquitted")
                    .replace("%moder%", moderator.getName()));
            ChatUtils.sendMessage(moderator, ConfigUtils.getMessage("cheatCheck.messagesToModer.youAcquittedSuspect")
                    .replace("%suspect%", suspect.getName()));

            cheatCheck.playSoundForSuspect("Sounds.OnSuspectAcquitted");

            if (ACTIVE_CHECKS.size() == 0) {
                HandlerList.unregisterAll(PLUGIN.getCheckListeners());
            }

        } else
            ChatUtils.sendMessage(moderator, ConfigUtils.getMessage("errors.cannotStopNotStartedCheatCheck"));

    }

    // Признать игрока виновным
    public void condemnPlayer(Player suspect, Player moderator) {

        if (ACTIVE_CHECKS.containsKey(suspect)) {

            CheatCheck cheatCheck = ACTIVE_CHECKS.get(suspect);

            if (EventUtils.callAndCheckEvent(new CheatCheckCondemnEvent(cheatCheck))) return;

            cheatCheck.stop();
            ACTIVE_CHECKS.remove(suspect);

            for (String punish : ConfigUtils.getStrings("AutoPunishments.Commands.OnSuspectCondemned")) {
                Bukkit.dispatchCommand(Bukkit.getConsoleSender(), ChatUtils.format(punish.replace("%suspect%", suspect.getName()), suspect));
            }

            ChatUtils.sendMessage(moderator, ConfigUtils.getMessage("cheatCheck.messagesToModer.youCondemnedSuspect")
                    .replace("%suspect%", suspect.getName()));

            if (ACTIVE_CHECKS.size() == 0) {
                HandlerList.unregisterAll(PLUGIN.getCheckListeners());
            }

        } else
            ChatUtils.sendMessage(moderator, ConfigUtils.getMessage("errors.cannotStopNotStartedCheatCheck"));

    }

    // Приостановить таймер проверки
    public void suspendCheck(Player suspect, Player moderator) {

        if (ACTIVE_CHECKS.containsKey(suspect)) {

            CheatCheck cheatCheck = ACTIVE_CHECKS.get(suspect);
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

        if (ACTIVE_CHECKS.containsKey(suspect)) {

            CheatCheck cheatCheck = ACTIVE_CHECKS.get(suspect);

            if (EventUtils.callAndCheckEvent(new CheatCheckConfessEvent(cheatCheck))) return;

            cheatCheck.stop();

            for (String punish : ConfigUtils.getStrings("AutoPunishments.Commands.OnSuspectConfess")) {
                Bukkit.dispatchCommand(Bukkit.getConsoleSender(), ChatUtils.format(punish.replace("%suspect%", suspect.getName()), suspect));
            }

            ChatUtils.sendMessage(cheatCheck.getModerator(), ConfigUtils.getMessage("cheatCheck.messagesToModer.suspectConfessed"));

        } else
            ChatUtils.sendMessage(suspect, ConfigUtils.getMessage("errors.youCannotConfess"));

    }

    public void playerContact(Player suspect, String contacts) {

        if (ACTIVE_CHECKS.containsKey(suspect)) {

            CheatCheck cheatCheck = ACTIVE_CHECKS.get(suspect);
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

        CheatCheck cheatCheck = ACTIVE_CHECKS.get(suspect);

        cheatCheck.stop();
        ACTIVE_CHECKS.remove(suspect);

        for (String punish : ConfigUtils.getStrings("AutoPunishments.Commands.OnSuspectQuit")) {
            Bukkit.dispatchCommand(Bukkit.getConsoleSender(), ChatUtils.format(punish.replace("%suspect%", suspect.getName()), suspect));
        }

        ChatUtils.sendMessage(cheatCheck.getModerator(), ConfigUtils.getMessage("cheatCheck.messagesToModer.suspectQuit")
                .replace("%suspect%", suspect.getName()));

    }

    public void moderQuit(Player moder) {

        CheatCheck cheatCheck = findByModer(moder);

        cheatCheck.stop();
        Player suspect = cheatCheck.getSuspect();
        ACTIVE_CHECKS.remove(suspect);

        ChatUtils.sendMessage(suspect, ConfigUtils.getMessage("cheatCheck.messagesToSuspect.moderQuit")
                .replace("%moder%", cheatCheck.getModerator().getName()));

    }

    public void timerExpired(Player suspect) {

        CheatCheck cheatCheck = ACTIVE_CHECKS.get(suspect);

        cheatCheck.timerExpired();

        for (String punish : ConfigUtils.getStrings("AutoPunishments.Commands.OnSuspect`sTimerExpired")) {
            Bukkit.dispatchCommand(Bukkit.getConsoleSender(), ChatUtils.format(punish.replace("%suspect%", suspect.getName()), suspect));
        }

        ChatUtils.sendMessage(cheatCheck.getModerator(), ConfigUtils.getMessage("cheatCheck.messagesToModer.suspectsTimerExpired"));

    }

    public void completionAllChecks() {

        if (ACTIVE_CHECKS.size() > 0) {

            PLUGIN.getLogger().info("Completing all active checks ...");

            for (CheatCheck cheatCheck : ACTIVE_CHECKS.values()) {
                cheatCheck.stop();
            }

            ACTIVE_CHECKS.clear();

        }

    }

    public boolean isChecking(Player suspect) {
        return ACTIVE_CHECKS.containsKey(suspect);
    }

    public boolean isModer(Player player) {
        for (CheatCheck cheatCheck : ACTIVE_CHECKS.values())
            if (cheatCheck.getModerator() == player)
                return true;
        return false;
    }

    private CheatCheck findByModer(Player moder) {
        for (CheatCheck cheatCheck : ACTIVE_CHECKS.values()) {
            if (cheatCheck.getModerator() == moder) {
                return cheatCheck;
            }
        }
        return null;
    }

    public CheatCheck findBySuspect(Player suspect) {
        return ACTIVE_CHECKS.get(suspect);
    }

    public static CheatCheckManager getInstance() {
        if (instance == null) {
            instance = new CheatCheckManager();
        }
        return instance;
    }

}