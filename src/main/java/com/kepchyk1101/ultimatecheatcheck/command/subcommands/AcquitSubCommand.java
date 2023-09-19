package com.kepchyk1101.ultimatecheatcheck.command.subcommands;

import com.kepchyk1101.ultimatecheatcheck.cheatcheck.CheatCheckManager;
import com.kepchyk1101.ultimatecheatcheck.utils.ChatUtils;
import com.kepchyk1101.ultimatecheatcheck.utils.ConfigUtils;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

public class AcquitSubCommand implements SubCommand {

    @Override
    public boolean onSubCommand(@NotNull CommandSender commandSender, @NotNull String[] args) {

        if (commandSender instanceof Player && commandSender.hasPermission("ucc.acquit")) {

            if (args.length == 0) {

                ChatUtils.sendMessage(commandSender, ConfigUtils.getMessage("wrongCommandUsages.acquit"));

            } else {

                Player suspect = Bukkit.getPlayer(args[0]);

                if (suspect != null)

                    CheatCheckManager.acquitPlayer(suspect, (Player) commandSender);

                else
                    ChatUtils.sendMessage(commandSender, ConfigUtils.getMessage("errors.playerNotFound"));

            }

        }

        return true;

    }

    @Override
    public String getName() {
        return "acquit";
    }

}