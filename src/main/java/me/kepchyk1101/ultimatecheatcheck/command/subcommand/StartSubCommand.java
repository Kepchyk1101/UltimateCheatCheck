package me.kepchyk1101.ultimatecheatcheck.command.subcommand;

import me.kepchyk1101.ultimatecheatcheck.managers.CheatCheckManager;
import me.kepchyk1101.ultimatecheatcheck.util.ChatUtils;
import me.kepchyk1101.ultimatecheatcheck.util.ConfigUtils;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

public class StartSubCommand implements SubCommand {

    @Override
    public boolean onSubCommand(@NotNull CommandSender commandSender, @NotNull String[] args) {

        final Player suspect = Bukkit.getPlayer(args[0]);
        if (suspect != null) {

            if (!(suspect == commandSender)) {

                CheatCheckManager.getInstance().callPlayer(suspect, (Player) commandSender);

            } else
                ChatUtils.sendMessage(commandSender, ConfigUtils.getMessage("errors.cannotSummonYourself"));

        } else
            ChatUtils.sendMessage(commandSender, ConfigUtils.getMessage("errors.playerNotFound"));

        return true;

    }

    @Override
    public String getName() {
        return "start";
    }

    @Override
    public String getPermission() {
        return "ucc.start";
    }

    @Override
    public int requiredArgs() {
        return 1;
    }

    @Override
    public String usage() {
        return ConfigUtils.getMessage("wrongCommandUsages.start");
    }

    @Override
    public boolean onlyPlayer() {
        return true;
    }

}