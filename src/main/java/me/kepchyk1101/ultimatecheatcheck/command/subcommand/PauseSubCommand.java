package me.kepchyk1101.ultimatecheatcheck.command.subcommand;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import me.kepchyk1101.ultimatecheatcheck.service.CheckService;
import me.kepchyk1101.ultimatecheatcheck.util.ChatUtils;
import me.kepchyk1101.ultimatecheatcheck.util.ConfigUtils;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class PauseSubCommand implements SubCommand {

    @NotNull
    CheckService checkService;

    @Override
    public boolean onSubCommand(@NotNull CommandSender commandSender, @NotNull String[] args) {

        final Player suspect = Bukkit.getPlayer(args[0]);
        if (suspect != null)
            checkService.suspendCheck(suspect, (Player) commandSender);
        else
            ChatUtils.sendMessage(commandSender, ConfigUtils.getMessage("errors.playerNotFound"));

        return true;

    }

    @Override
    public String getName() {
        return "pause";
    }

    @Override
    public String getPermission() {
        return "ucc.pause";
    }

    @Override
    public int requiredArgs() {
        return 1;
    }

    @Override
    public boolean onlyPlayer() {
        return true;
    }

    @Override
    public String usage() {
        return ConfigUtils.getMessage("wrongCommandUsages.pause");
    }

}