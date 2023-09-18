package com.kepchyk1101.ultimatecheatcheck.command;

import com.kepchyk1101.ultimatecheatcheck.cheatcheck.CheatCheckManager;
import com.kepchyk1101.ultimatecheatcheck.utils.PlayerUtils;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class UCCTab implements TabCompleter {

    @Override
    public @Nullable List<String> onTabComplete(@NotNull CommandSender commandSender, @NotNull Command command, @NotNull String s, @NotNull String[] args) {
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

                if (args[0].equals("start") && PlayerUtils.hasPermission(commandSender, "ucc.start", "ucc.moder", "ucc.*")) {
                    List<String> players = new ArrayList<>();
                    for (Player player : Bukkit.getOnlinePlayers())
                        if (!player.hasPermission("ucc.immunity") && !CheatCheckManager.isChecking(player))
                            players.add(player.getName());
                    players.remove(commandSender.getName());
                    return players;
                } else if (args[0].equals("acquit") && PlayerUtils.hasPermission(commandSender, "ucc.acquit", "ucc.moder", "ucc.*")) {
                    return getCheckingPlayers((Player) commandSender);
                } else if (args[0].equals("condemn") && PlayerUtils.hasPermission(commandSender, "ucc.condemn", "ucc.moder", "ucc.*")) {
                    return getCheckingPlayers((Player) commandSender);
                } else if (args[0].equals("pause") && PlayerUtils.hasPermission(commandSender, "ucc.pause", "ucc.moder", "ucc.*")) {
                    List<String> players = new ArrayList<>();
                    for (Player player : Bukkit.getOnlinePlayers())
                        if (CheatCheckManager.isChecking(player) &&
                                !CheatCheckManager.findBySuspect(player).isPaused())
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
            if (CheatCheckManager.isChecking(player))
                players.add(player.getName());
        players.remove(moderator.getName());
        return players;
    }

}