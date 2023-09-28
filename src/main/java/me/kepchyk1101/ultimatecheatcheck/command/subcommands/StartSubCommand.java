package me.kepchyk1101.ultimatecheatcheck.command.subcommands;

import me.kepchyk1101.ultimatecheatcheck.cheatcheck.CheatCheckManager;
import me.kepchyk1101.ultimatecheatcheck.util.ChatUtils;
import me.kepchyk1101.ultimatecheatcheck.util.ConfigUtils;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

public class StartSubCommand implements SubCommand {

    @Override
    public boolean onSubCommand(@NotNull CommandSender commandSender, @NotNull String[] args) {

        if (commandSender instanceof Player) {

            if (commandSender.hasPermission("ucc.start")) {

                if (args.length == 0) {

                    ChatUtils.sendMessage(commandSender, ConfigUtils.getMessage("wrongCommandUsages.start"));

                } else {

                    Player suspect = Bukkit.getPlayer(args[0]);

                    if (suspect != null) {

                        if (!(suspect == commandSender)) {

                            CheatCheckManager.callPlayer(suspect, (Player) commandSender);

                        } else
                            ChatUtils.sendMessage(commandSender, ConfigUtils.getMessage("errors.cannotSummonYourself"));

                    } else
                        ChatUtils.sendMessage(commandSender, ConfigUtils.getMessage("errors.playerNotFound"));

                }

            } else
                ChatUtils.sendMessage(commandSender, ConfigUtils.getMessage("errors.noPermission"));

        } else
            ChatUtils.sendMessage(commandSender, ConfigUtils.getMessage("errors.commandCanUsedOnlyByPlayer"));

        return true;

    }

    @Override
    public String getName() {
        return "start";
    }

}