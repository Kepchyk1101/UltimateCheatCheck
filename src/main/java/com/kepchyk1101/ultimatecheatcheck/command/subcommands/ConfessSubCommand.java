package com.kepchyk1101.ultimatecheatcheck.command.subcommands;

import com.kepchyk1101.ultimatecheatcheck.cheatcheck.CheatCheckManager;
import com.kepchyk1101.ultimatecheatcheck.utils.PlayerUtils;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

public class ConfessSubCommand implements SubCommand {

    @Override
    public boolean onSubCommand(@NotNull CommandSender commandSender, @NotNull String[] args) {

        if (PlayerUtils.isPlayer(commandSender)) {

            CheatCheckManager.suspectConfess((Player) commandSender);

        }

        return true;

    }

}