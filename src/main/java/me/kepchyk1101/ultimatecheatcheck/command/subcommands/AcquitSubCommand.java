package me.kepchyk1101.ultimatecheatcheck.command.subcommands;

import me.kepchyk1101.ultimatecheatcheck.managers.CheatCheckManager;
import me.kepchyk1101.ultimatecheatcheck.util.ChatUtils;
import me.kepchyk1101.ultimatecheatcheck.util.ConfigUtils;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

public class AcquitSubCommand implements SubCommand {

    @Override
    public boolean onSubCommand(@NotNull CommandSender commandSender, @NotNull String[] args) {

        final Player suspect = Bukkit.getPlayer(args[0]);
        if (suspect != null)
            CheatCheckManager.getInstance().acquitPlayer(suspect, (Player) commandSender);
        else
            ChatUtils.sendMessage(commandSender, ConfigUtils.getMessage("errors.playerNotFound"));

        return true;

    }

    @Override
    public String getName() {
        return "acquit";
    }

    @Override
    public String getPermission() {
        return "ucc.acquit";
    }

    @Override
    public int requiredArgs() {
        return 1;
    }

    @Override
    public String usage() {
        return ConfigUtils.getMessage("wrongCommandUsages.acquit");
    }

    @Override
    public boolean onlyPlayer() {
        return true;
    }

}