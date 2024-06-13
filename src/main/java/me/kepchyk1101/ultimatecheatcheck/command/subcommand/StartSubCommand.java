package me.kepchyk1101.ultimatecheatcheck.command.subcommand;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import me.kepchyk1101.ultimatecheatcheck.service.CheckService;
import me.kepchyk1101.ultimatecheatcheck.service.LaterCheckService;
import me.kepchyk1101.ultimatecheatcheck.util.ChatUtils;
import me.kepchyk1101.ultimatecheatcheck.util.ConfigUtils;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class StartSubCommand implements SubCommand {

    @NotNull CheckService checkService;
    @NotNull LaterCheckService laterCheckService;

    @Override
    public boolean onSubCommand(@NotNull CommandSender commandSender, @NotNull String[] args) {

        String argsString = String.join(" ", args);
        boolean later = argsString.contains(" -l") || argsString.contains(" -later");
        boolean force = argsString.contains(" -f") || argsString.contains(" -force");

        if (later && !commandSender.getName().equals(args[0])) {

            if (!commandSender.hasPermission("ucc.start.force")) {
                ChatUtils.sendMessage(commandSender, ConfigUtils.getMessage("errors.noPermission"));
                return true;
            }

            if (!ConfigUtils.getBoolean("experimental.later-checks-enabled")) {
                ChatUtils.sendMessage(commandSender, ConfigUtils.getMessage("errors.function-disabled")
                        .replace("%func-name%", "experimental.later-checks-enabled"));
                return true;
            }

            OfflinePlayer suspect = Bukkit.getOfflinePlayer(args[0]);
            if (!suspect.hasPlayedBefore()) {
                ChatUtils.sendMessage(commandSender, ConfigUtils.getMessage("errors.never-played-before"));
                return true;
            }

            laterCheckService.startLater(suspect, (Player) commandSender);

            return true;
        }

        Player suspect = Bukkit.getPlayer(args[0]);
        if (suspect == null) {
            ChatUtils.sendMessage(commandSender, ConfigUtils.getMessage("errors.playerNotFound"));
            return true;
        }

        if (suspect == commandSender) {
            ChatUtils.sendMessage(commandSender, ConfigUtils.getMessage("errors.cannotSummonYourself"));
            return true;
        }

        checkService.start(suspect, (Player) commandSender, force);

        return true;

    }

    @Override
    public String getName() {
        return "start";
    }

    @Override
    public String getPermission() {
        return "ucc.start";
    }

    @Override
    public int requiredArgs() {
        return 1;
    }

    @Override
    public String usage() {
        return ConfigUtils.getMessage("wrongCommandUsages.start");
    }

    @Override
    public boolean onlyPlayer() {
        return true;
    }

}