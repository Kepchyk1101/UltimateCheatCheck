package com.kepchyk1101.ultimatecheatcheck.command.subcommands;

import com.kepchyk1101.ultimatecheatcheck.cheatcheck.CheatCheckManager;
import com.kepchyk1101.ultimatecheatcheck.utils.ChatUtils;
import com.kepchyk1101.ultimatecheatcheck.utils.ConfigUtils;
import com.kepchyk1101.ultimatecheatcheck.utils.PlayerUtils;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

public class ContactSubCommand implements SubCommand {

    @Override
    public boolean onSubCommand(@NotNull CommandSender commandSender, @NotNull String[] args) {

        if (PlayerUtils.isPlayer(commandSender)) {

            if (args.length == 0) {

                ChatUtils.sendMessage(commandSender, ConfigUtils.getMessage("wrongCommandUsages.contact"));

            } else {

                CheatCheckManager.playerContact((Player) commandSender, args[0]);

            }

        }

        return true;

    }

}