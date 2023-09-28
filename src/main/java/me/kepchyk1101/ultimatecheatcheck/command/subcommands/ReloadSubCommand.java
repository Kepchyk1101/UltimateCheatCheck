package me.kepchyk1101.ultimatecheatcheck.command.subcommands;

import me.kepchyk1101.ultimatecheatcheck.UltimateCheatCheck;
import me.kepchyk1101.ultimatecheatcheck.util.ChatUtils;
import me.kepchyk1101.ultimatecheatcheck.util.ConfigUtils;
import org.bukkit.command.CommandSender;
import org.jetbrains.annotations.NotNull;

public class ReloadSubCommand implements SubCommand{

    @Override
    public boolean onSubCommand(@NotNull CommandSender commandSender, @NotNull String[] args) {

        if (commandSender.hasPermission("ucc.reload")) {

            UltimateCheatCheck.getInstance().reloadConfigs();
            ChatUtils.sendMessage(commandSender, ConfigUtils.getMessage("misc.configsSuccessfullyReloaded"));

        } else
            ChatUtils.sendMessage(commandSender, ConfigUtils.getMessage("errors.noPermission"));

        return true;

    }

    @Override
    public String getName() {
        return "reload";
    }

}