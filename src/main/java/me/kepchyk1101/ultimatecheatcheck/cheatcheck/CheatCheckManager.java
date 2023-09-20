package me.kepchyk1101.ultimatecheatcheck.cheatcheck;

import me.kepchyk1101.ultimatecheatcheck.UltimateCheatCheck;
import me.kepchyk1101.ultimatecheatcheck.events.*;
import me.kepchyk1101.ultimatecheatcheck.listeners.CheckListeners;
import me.kepchyk1101.ultimatecheatcheck.utils.ChatUtils;
import me.kepchyk1101.ultimatecheatcheck.utils.ConfigUtils;
import me.kepchyk1101.ultimatecheatcheck.utils.EventUtils;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.HandlerList;

import java.util.HashMap;
import java.util.Map;

public class CheatCheckManager {

    private static final Map<Player, CheatCheck> activeChecks = new HashMap<>();

    public static void callPlayer(Player suspect, Player moderator) {

        if (!activeChecks.containsKey(suspect)) {

            if (!suspect.hasPermission("ucc.immunity")) {

                CheatCheck cheatCheck = new CheatCheck(suspect, moderator);

                if (EventUtils.callAndCheckEvent(new CheatCheckStartEvent(cheatCheck))) return;

                cheatCheck.start();
                activeChecks.put(suspect, cheatCheck);

                for (String message : ConfigUtils.getMessages("cheatCheck.messagesToSuspect.youCalledForCheck"))
                    ChatUtils.sendMessage(suspect, message
                            .replace("%time%", String.valueOf(cheatCheck.getTimer()))
                            .replace("%moder%", cheatCheck.getModerator().getName()));
                ChatUtils.sendMessage(moderator, ConfigUtils.getMessage("cheatCheck.messagesToModer.youStartedChecking")
                        .replace("%suspect%", suspect.getName()));

                if (activeChecks.size() == 1)
                    Bukkit.getPluginManager().registerEvents(new CheckListeners(), UltimateCheatCheck.getInstance());

            } else
                ChatUtils.sendMessage(moderator, ConfigUtils.getMessage("errors.suspectHasImmunity"));

        } else
            ChatUtils.sendMessage(moderator, ConfigUtils.getMessage("errors.suspectAlreadyOnCheatCheck"));

    }

    // Признать игрока невиновным
    public static void acquitPlayer(Player suspect, Player moderator) {

        if (activeChecks.containsKey(suspect)) {

            CheatCheck cheatCheck = activeChecks.get(suspect);

            if (EventUtils.callAndCheckEvent(new CheatCheckAcquitEvent(cheatCheck))) return;

            cheatCheck.stop();
            activeChecks.remove(suspect);

            ChatUtils.sendMessage(suspect, ConfigUtils.getMessage("cheatCheck.messagesToSuspect.youAcquitted")
                    .replace("%moder%", suspect.getName()));
            ChatUtils.sendMessage(moderator, ConfigUtils.getMessage("cheatCheck.messagesToModer.youAcquittedSuspect")
                    .replace("%suspect%", moderator.getName()));

            if (activeChecks.size() == 0)
                HandlerList.unregisterAll(new CheckListeners());

        } else
            ChatUtils.sendMessage(moderator, ConfigUtils.getMessage("errors.cannotStopNotStartedCheatCheck"));

    }

    // Признать игрока виновным
    public static void condemnPlayer(Player suspect, Player moderator) {

        if (activeChecks.containsKey(suspect)) {

            CheatCheck cheatCheck = activeChecks.get(suspect);

            if (EventUtils.callAndCheckEvent(new CheatCheckCondemnEvent(cheatCheck))) return;

            cheatCheck.stop();
            activeChecks.remove(suspect);

            for (String punish : ConfigUtils.getStrings("AutoPunishments.Commands.OnSuspectCondemned"))
                Bukkit.dispatchCommand(Bukkit.getConsoleSender(), ChatUtils.format(punish.replace("%suspect%", suspect.getName()), suspect));

            ChatUtils.sendMessage(moderator, ConfigUtils.getMessage("cheatCheck.messagesToModer.youCondemnedSuspect")
                    .replace("%suspect%", suspect.getName()));

            if (activeChecks.size() == 0)
                HandlerList.unregisterAll(new CheckListeners());

        } else
            ChatUtils.sendMessage(moderator, ConfigUtils.getMessage("errors.cannotCancelNotStartedCheatCheck"));

    }

    // Приостановить таймер проверки
    public static void suspendCheck(Player suspect, Player moderator) {

        if (activeChecks.containsKey(suspect)) {

            CheatCheck cheatCheck = activeChecks.get(suspect);
            if (!cheatCheck.isPaused()) {

                if (EventUtils.callAndCheckEvent(new CheatCheckPauseEvent(cheatCheck))) return;

                cheatCheck.pause();

                ChatUtils.sendMessage(suspect, ConfigUtils.getMessage("cheatCheck.messagesToSuspect.yourCheckPaused")
                        .replace("%moder%", suspect.getName()));
                ChatUtils.sendMessage(moderator, ConfigUtils.getMessage("cheatCheck.messagesToModer.youPausedChecking")
                        .replace("%suspect%", suspect.getName()));

            } else
                ChatUtils.sendMessage(moderator, ConfigUtils.getMessage("errors.cannotPauseNotPausedCheatCheck"));

        } else
            ChatUtils.sendMessage(moderator, ConfigUtils.getMessage("errors.cannotPauseNotStartedCheatCheck"));

    }

    // Игрок признался самостоятельно
    public static void suspectConfess(Player suspect) {

        if (activeChecks.containsKey(suspect)) {

            CheatCheck cheatCheck = activeChecks.get(suspect);

            if (EventUtils.callAndCheckEvent(new CheatCheckConfessEvent(cheatCheck))) return;

            cheatCheck.stop();

            for (String punish : ConfigUtils.getStrings("AutoPunishments.Commands.OnSuspectConfess"))
                Bukkit.dispatchCommand(Bukkit.getConsoleSender(), ChatUtils.format(punish.replace("%suspect%", suspect.getName()), suspect));

            ChatUtils.sendMessage(cheatCheck.getModerator(), ConfigUtils.getMessage("cheatCheck.messagesToModer.suspectConfessed"));

        } else
            ChatUtils.sendMessage(suspect, ConfigUtils.getMessage("errors.youCannotConfess"));

    }

    public static void playerContact(Player suspect, String contacts) {

        if (activeChecks.containsKey(suspect)) {

            CheatCheck cheatCheck = activeChecks.get(suspect);
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

    public static void suspectQuit(Player suspect) {

        CheatCheck cheatCheck = activeChecks.get(suspect);

        cheatCheck.stop();
        activeChecks.remove(suspect);

        for (String punish : ConfigUtils.getStrings("AutoPunishments.Commands.OnSuspectQuit"))
            Bukkit.dispatchCommand(Bukkit.getConsoleSender(), ChatUtils.format(punish.replace("%suspect%", suspect.getName()), suspect));

        ChatUtils.sendMessage(cheatCheck.getModerator(), ConfigUtils.getMessage("cheatCheck.messagesToModer.suspectQuit")
                .replace("%suspect%", suspect.getName()));

    }

    public static void moderQuit(Player moder) {

        CheatCheck cheatCheck = findByModer(moder);

        cheatCheck.stop();
        Player suspect = cheatCheck.getSuspect();
        activeChecks.remove(suspect);

        ChatUtils.sendMessage(suspect, ConfigUtils.getMessage("cheatCheck.messagesToSuspect.moderQuit")
                .replace("%moder%", cheatCheck.getModerator().getName()));

    }

    public static void timerExpired(Player suspect) {

        CheatCheck cheatCheck = activeChecks.get(suspect);

        cheatCheck.timerExpired();

        for (String punish : ConfigUtils.getStrings("AutoPunishments.Commands.OnSuspect`sTimerExpired"))
            Bukkit.dispatchCommand(Bukkit.getConsoleSender(), ChatUtils.format(punish.replace("%suspect%", suspect.getName()), suspect));

        ChatUtils.sendMessage(cheatCheck.getModerator(), ConfigUtils.getMessage("cheatCheck.messagesToModer.suspectsTimerExpired"));

    }

    public static void completionAllChecks() {

        if (activeChecks.size() > 0) {

            UltimateCheatCheck.getInstance().getLogger().info("Completing all active checks ...");

            for (CheatCheck cheatCheck : activeChecks.values()) {
                cheatCheck.stop();
            }

            activeChecks.clear();

        }

    }

    public static boolean isChecking(Player suspect) {
        return activeChecks.containsKey(suspect);
    }

    public static boolean isModer(Player player) {
        for (CheatCheck cheatCheck : activeChecks.values())
            if (cheatCheck.getModerator() == player)
                return true;
        return false;
    }

    private static CheatCheck findByModer(Player moder) {
        for (CheatCheck cheatCheck : activeChecks.values())
            if (cheatCheck.getModerator() == moder)
                return cheatCheck;
        return null;
    }

    public static CheatCheck findBySuspect(Player suspect) {
        return activeChecks.get(suspect);
    }

}