package me.kepchyk1101.ultimatecheatcheck.command.subcommands;

import me.kepchyk1101.ultimatecheatcheck.managers.CheatCheckManager;
import me.kepchyk1101.ultimatecheatcheck.util.ChatUtils;
import me.kepchyk1101.ultimatecheatcheck.util.ConfigUtils;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

public class ConfessSubCommand implements SubCommand {

    @Override
    public boolean onSubCommand(@NotNull CommandSender commandSender, @NotNull String[] args) {

        CheatCheckManager.getInstance().suspectConfess((Player) commandSender);

        return true;

    }

    @Override
    public String getName() {
        return "confess";
    }

    @Override
    public String getPermission() {
        return null;
    }

    @Override
    public int requiredArgs() {
        return 0;
    }

    @Override
    public String usage() {
        return ConfigUtils.getMessage("wrongCommandUsages");
    }

    @Override
    public boolean onlyPlayer() {
        return true;
    }

}