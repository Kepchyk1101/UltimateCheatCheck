package com.kepchyk1101.ultimatecheatcheck.command;

import com.kepchyk1101.ultimatecheatcheck.command.subcommands.*;
import com.kepchyk1101.ultimatecheatcheck.utils.ChatUtils;
import com.kepchyk1101.ultimatecheatcheck.utils.ConfigUtils;
import com.kepchyk1101.ultimatecheatcheck.utils.PermissionUtils;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.jetbrains.annotations.NotNull;

import java.util.Arrays;

public class UCCExecutor implements CommandExecutor {

    private final ReloadSubCommand reloadSubCommand;
    private final StartSubCommand startSubCommand;
    private final AcquitSubCommand acquitSubCommand;
    private final CondemnSubCommand condemnSubCommand;
    private final PauseSubCommand pauseSubCommand;
    private final ContactSubCommand contactSubCommand;
    private final ConfessSubCommand confessSubCommand;

    public UCCExecutor() {
        this.reloadSubCommand = new ReloadSubCommand();
        this.startSubCommand = new StartSubCommand();
        this.acquitSubCommand = new AcquitSubCommand();
        this.pauseSubCommand = new PauseSubCommand();
        this.condemnSubCommand = new CondemnSubCommand();
        this.contactSubCommand = new ContactSubCommand();
        this.confessSubCommand = new ConfessSubCommand();
    }

    @Override
    public boolean onCommand(@NotNull CommandSender commandSender, @NotNull Command command, @NotNull String s, @NotNull String[] args) {

        if (args.length > 0) {

            final String subCommand = args[0];
            switch (subCommand) {
                case "reload":
                    return reloadSubCommand.onSubCommand(commandSender, Arrays.copyOfRange(args, 1, args.length));
                case "start":
                    return startSubCommand.onSubCommand(commandSender, Arrays.copyOfRange(args, 1, args.length));
                case "acquit":
                    return acquitSubCommand.onSubCommand(commandSender, Arrays.copyOfRange(args, 1, args.length));
                case "condemn":
                    return condemnSubCommand.onSubCommand(commandSender, Arrays.copyOfRange(args, 1, args.length));
                case "pause":
                    return pauseSubCommand.onSubCommand(commandSender, Arrays.copyOfRange(args, 1, args.length));
                case "contact":
                    return contactSubCommand.onSubCommand(commandSender, Arrays.copyOfRange(args, 1, args.length));
                case "confess":
                    return confessSubCommand.onSubCommand(commandSender, Arrays.copyOfRange(args, 1, args.length));
            }

        }

        if (PermissionUtils.hasPermission(commandSender, "ucc.help", "ucc.moder", "ucc.*"))
            for (String message : ConfigUtils.getMessages("misc.helpCommandMessage"))
                ChatUtils.sendMessage(commandSender, message);

        return true;

    }

}