package me.kepchyk1101.ultimatecheatcheck.command.subcommands;

import org.bukkit.command.CommandSender;
import org.jetbrains.annotations.NotNull;

public interface SubCommand {

    boolean onSubCommand(@NotNull CommandSender commandSender, @NotNull String[] args);

    String getName();

    //String getUsage();

    //String getPermission();

}