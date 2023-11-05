package me.kepchyk1101.ultimatecheatcheck.command;

import me.kepchyk1101.ultimatecheatcheck.command.subcommand.*;
import me.kepchyk1101.ultimatecheatcheck.managers.CheatCheckManager;
import me.kepchyk1101.ultimatecheatcheck.util.ChatUtils;
import me.kepchyk1101.ultimatecheatcheck.util.ConfigUtils;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabExecutor;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class UCCCommand implements TabExecutor {

    private final List<SubCommand> subCommands;
    private final CheatCheckManager cheatCheckManager = CheatCheckManager.getInstance();

    public UCCCommand() {
        subCommands = new ArrayList<>();
        subCommands.addAll(Arrays.asList(
                new ReloadSubCommand(),
                new StartSubCommand(),
                new AcquitSubCommand(),
                new CondemnSubCommand(),
                new PauseSubCommand(),
                new ContactSubCommand(),
                new ConfessSubCommand()));
    }

    @Override
    public boolean onCommand(@NotNull CommandSender commandSender, @NotNull Command command, @NotNull String s, @NotNull String[] args) {

        if (args.length > 0) {

            final String subCommandArg = args[0];
            for (SubCommand subCommand : subCommands) {
                if (subCommand.getName().equalsIgnoreCase(subCommandArg)) {

                    if (subCommand.onlyPlayer() && !(commandSender instanceof Player)) {
                        ChatUtils.sendMessage(commandSender, ConfigUtils.getMessage("errors.commandCanUsedOnlyByPlayer"));
                        return true;
                    }

                    final String subCommandPermission = subCommand.getPermission();
                    if (subCommandPermission != null && !commandSender.hasPermission(subCommandPermission)) {
                        ChatUtils.sendMessage(commandSender, ConfigUtils.getMessage("errors.noPermission"));
                        return true;
                    }

                    if (args.length - 1 < subCommand.requiredArgs()) {
                        ChatUtils.sendMessage(commandSender, subCommand.usage());
                        return true;
                    }

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

    @Nullable
    @Override
    public List<String> onTabComplete(@NotNull CommandSender commandSender, @NotNull Command command, @NotNull String s, @NotNull String[] args) {
        return filter(complete(commandSender, args), args);
    }

    private List<String> complete(CommandSender commandSender, String[] args) {

        switch (args.length) {

            case 1:

                if (commandSender.hasPermission("ucc.*"))
                    return Arrays.asList("reload", "start", "acquit", "condemn", "pause", "contact", "confess");
                else if (commandSender.hasPermission("ucc.moder"))
                    return Arrays.asList("start", "acquit", "condemn", "pause");

                List<String> subCommands = new ArrayList<>();

                if (commandSender.hasPermission("ucc.reload"))
                    subCommands.add("reload");
                if (commandSender.hasPermission("ucc.start"))
                    subCommands.add("start");
                if (commandSender.hasPermission("ucc.acquit"))
                    subCommands.add("acquit");
                if (commandSender.hasPermission("ucc.condemn"))
                    subCommands.add("condemn");
                if (commandSender.hasPermission("ucc.pause"))
                    subCommands.add("pause");
                if (commandSender.hasPermission("ucc.contact"))
                    subCommands.add("contact");
                if (commandSender.hasPermission("ucc.confess"))
                    subCommands.add("confess");

                return subCommands;

            case 2:

                if (args[0].equals("start") && commandSender.hasPermission("ucc.start")) {
                    List<String> players = new ArrayList<>();
                    for (Player player : Bukkit.getOnlinePlayers())
                        if (!player.hasPermission("ucc.immunity") && !cheatCheckManager.isChecking(player))
                            players.add(player.getName());
                    players.remove(commandSender.getName());
                    return players;
                } else if (args[0].equals("acquit") && commandSender.hasPermission("ucc.acquit")) {
                    return getCheckingPlayers((Player) commandSender);
                } else if (args[0].equals("condemn") && commandSender.hasPermission("ucc.condemn")) {
                    return getCheckingPlayers((Player) commandSender);
                } else if (args[0].equals("pause") && commandSender.hasPermission("ucc.pause")) {
                    List<String> players = new ArrayList<>();
                    for (Player player : Bukkit.getOnlinePlayers())
                        if (cheatCheckManager.isChecking(player) &&
                                !cheatCheckManager.findBySuspect(player).isPaused())
                            players.add(player.getName());
                    players.remove(commandSender.getName());
                    return players;
                }

        }

        return new ArrayList<>();

    }

    // Фильтр выбирает только те команды, которые начинаются с первой введённой буквы
    private List<String> filter(List<String> list, String[] args) {

        if (list == null)
            return null;
        String last = args[args.length - 1];
        List<String> result = new ArrayList<>();
        for (String arg : list)
            if (arg.toLowerCase().startsWith(last.toLowerCase()))
                result.add(arg);
        return result;

    }

    private List<String> getCheckingPlayers(Player moderator) {
        List<String> players = new ArrayList<>();
        for (Player player : Bukkit.getOnlinePlayers())
            if (cheatCheckManager.isChecking(player))
                players.add(player.getName());
        players.remove(moderator.getName());
        return players;
    }

}