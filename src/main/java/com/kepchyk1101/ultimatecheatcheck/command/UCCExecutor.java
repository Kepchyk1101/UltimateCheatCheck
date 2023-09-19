package com.kepchyk1101.ultimatecheatcheck.command;

import com.kepchyk1101.ultimatecheatcheck.command.subcommands.*;
import com.kepchyk1101.ultimatecheatcheck.utils.ChatUtils;
import com.kepchyk1101.ultimatecheatcheck.utils.ConfigUtils;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class UCCExecutor implements CommandExecutor {

    private final List<SubCommand> subCommands;

    public UCCExecutor() {
        subCommands = new ArrayList<>();
        subCommands.addAll(Arrays.asList(
                new ReloadSubCommand(), //todo
                new StartSubCommand(),
                new AcquitSubCommand(), //todo
                new CondemnSubCommand(), //todo
                new PauseSubCommand(), //todo
                new ContactSubCommand(), //todo
                new ConfessSubCommand())); //todo
    }

    @Override
    public boolean onCommand(@NotNull CommandSender commandSender, @NotNull Command command, @NotNull String s, @NotNull String[] args) {

        if (args.length > 0) {

            final String subCommandArg = args[0];
            for (SubCommand subCommand : subCommands) {
                if (subCommand.getName().equalsIgnoreCase(subCommandArg)) {
                    return subCommand.onSubCommand(commandSender, Arrays.copyOfRange(args, 1, args.length));
                }
            }

        }

        if (commandSender.hasPermission("ucc.help")) {
            for (String message : ConfigUtils.getMessages("misc.helpCommandMessage")) {
                ChatUtils.sendMessage(commandSender, message);
            }
        } else {
            ChatUtils.sendMessage(commandSender, ConfigUtils.getMessage("errors.noPermission"));
        }

        return true;

    }

}