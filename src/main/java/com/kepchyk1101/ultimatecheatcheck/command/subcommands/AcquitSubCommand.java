package com.kepchyk1101.ultimatecheatcheck.command.subcommands;

import com.kepchyk1101.ultimatecheatcheck.cheatcheck.CheatCheckManager;
import com.kepchyk1101.ultimatecheatcheck.utils.ChatUtils;
import com.kepchyk1101.ultimatecheatcheck.utils.ConfigUtils;
import com.kepchyk1101.ultimatecheatcheck.utils.PermissionUtils;
import com.kepchyk1101.ultimatecheatcheck.utils.PlayerUtils;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

public class AcquitSubCommand implements SubCommand {

    @Override
    public boolean onSubCommand(@NotNull CommandSender commandSender, @NotNull String[] args) {

        if (PlayerUtils.isPlayer(commandSender) &&
                PermissionUtils.hasPermission(commandSender, "ucc.acquit", "ucc.moder", "ucc.*")) {

            if (args.length == 0) {

                if (PlayerUtils.isPlayer(commandSender) &&
                        PermissionUtils.hasPermission(commandSender,
                                "ucc.acquit", "ucc.moder", "ucc.*")) {

                    ChatUtils.sendMessage(commandSender, ConfigUtils.getMessage("wrongCommandUsages.acquit"));

                }

            } else{

                Player suspect = Bukkit.getPlayer(args[0]);

                if (suspect != null)

                    CheatCheckManager.acquitPlayer(suspect, (Player) commandSender);

                else
                    ChatUtils.sendMessage(commandSender, ConfigUtils.getMessage("errors.playerNotFound"));

            }

        }

        return true;

    }

}