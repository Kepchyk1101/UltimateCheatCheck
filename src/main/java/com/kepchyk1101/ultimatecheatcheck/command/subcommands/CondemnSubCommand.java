package com.kepchyk1101.ultimatecheatcheck.command.subcommands;

import com.kepchyk1101.ultimatecheatcheck.cheatcheck.CheatCheckManager;
import com.kepchyk1101.ultimatecheatcheck.utils.ChatUtils;
import com.kepchyk1101.ultimatecheatcheck.utils.ConfigUtils;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

public class CondemnSubCommand implements SubCommand {

    @Override
    public boolean onSubCommand(@NotNull CommandSender commandSender, @NotNull String[] args) {

        if (commandSender instanceof Player && commandSender.hasPermission("ucc.condemn")) {

            if (args.length == 0) {

                ChatUtils.sendMessage(commandSender, ConfigUtils.getMessage("wrongCommandUsages.condemn"));

            } else {

                Player suspect = Bukkit.getPlayer(args[0]);
                if (suspect != null)

                    CheatCheckManager.condemnPlayer(suspect, (Player) commandSender);

                else
                    ChatUtils.sendMessage(commandSender, ConfigUtils.getMessage("errors.playerNotFound"));

            }

        }

        return true;

    }

    @Override
    public String getName() {
        return "condemn";
    }

}