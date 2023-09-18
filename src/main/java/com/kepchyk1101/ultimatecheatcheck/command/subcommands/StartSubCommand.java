package com.kepchyk1101.ultimatecheatcheck.command.subcommands;

import com.kepchyk1101.ultimatecheatcheck.cheatcheck.CheatCheckManager;
import com.kepchyk1101.ultimatecheatcheck.utils.ChatUtils;
import com.kepchyk1101.ultimatecheatcheck.utils.ConfigUtils;
import com.kepchyk1101.ultimatecheatcheck.utils.PlayerUtils;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

public class StartSubCommand implements SubCommand {

    @Override
    public boolean onSubCommand(@NotNull CommandSender commandSender, @NotNull String[] args) {

        if (PlayerUtils.isPlayer(commandSender) &&
                PlayerUtils.hasPermission(commandSender, "ucc.start", "ucc.moder", "ucc.*")) {

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

        }

        return true;

    }

}
