package me.kepchyk1101.ultimatecheatcheck.command.subcommands;

import me.kepchyk1101.ultimatecheatcheck.cheatcheck.CheatCheckManager;
import me.kepchyk1101.ultimatecheatcheck.utils.ChatUtils;
import me.kepchyk1101.ultimatecheatcheck.utils.ConfigUtils;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

public class ConfessSubCommand implements SubCommand {

    @Override
    public boolean onSubCommand(@NotNull CommandSender commandSender, @NotNull String[] args) {

        if (commandSender instanceof Player) {

            CheatCheckManager.suspectConfess((Player) commandSender);

        } else
            ChatUtils.sendMessage(commandSender, ConfigUtils.getMessage("errors.commandCanUsedOnlyByPlayer"));

        return true;

    }

    @Override
    public String getName() {
        return "confess";
    }

}