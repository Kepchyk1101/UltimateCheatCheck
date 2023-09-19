package com.kepchyk1101.ultimatecheatcheck.command.subcommands;

import com.kepchyk1101.ultimatecheatcheck.UltimateCheatCheck;
import com.kepchyk1101.ultimatecheatcheck.utils.ChatUtils;
import com.kepchyk1101.ultimatecheatcheck.utils.ConfigUtils;
import org.bukkit.command.CommandSender;
import org.jetbrains.annotations.NotNull;

public class ReloadSubCommand implements SubCommand{

    @Override
    public boolean onSubCommand(@NotNull CommandSender commandSender, @NotNull String[] args) {

        if (commandSender.hasPermission("ucc.reload")) {

            UltimateCheatCheck.getInstance().reloadConfigs();
            ChatUtils.sendMessage(commandSender, ConfigUtils.getMessage("misc.configsSuccessfullyReloaded"));

        }

        return true;

    }

    @Override
    public String getName() {
        return "reload";
    }

}