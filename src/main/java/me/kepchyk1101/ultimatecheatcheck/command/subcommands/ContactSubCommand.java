package me.kepchyk1101.ultimatecheatcheck.command.subcommands;

import me.kepchyk1101.ultimatecheatcheck.managers.CheatCheckManager;
import me.kepchyk1101.ultimatecheatcheck.util.ChatUtils;
import me.kepchyk1101.ultimatecheatcheck.util.ConfigUtils;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

public class ContactSubCommand implements SubCommand {

    @Override
    public boolean onSubCommand(@NotNull CommandSender commandSender, @NotNull String[] args) {

        CheatCheckManager.getInstance().playerContact((Player) commandSender, args[0]);

        return true;

    }

    @Override
    public String getName() {
        return "contact";
    }

    @Override
    public String getPermission() {
        return null;
    }

    @Override
    public int requiredArgs() {
        return 1;
    }

    @Override
    public String usage() {
        return ConfigUtils.getMessage("wrongCommandUsages.contact");
    }

    @Override
    public boolean onlyPlayer() {
        return true;
    }

}