package me.kepchyk1101.ultimatecheatcheck.command.subcommand;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import me.kepchyk1101.ultimatecheatcheck.service.CheckService;
import me.kepchyk1101.ultimatecheatcheck.util.ConfigUtils;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class ConfessSubCommand implements SubCommand {

    @NotNull
    CheckService checkService;

    @Override
    public boolean onSubCommand(@NotNull CommandSender commandSender, @NotNull String[] args) {

        checkService.suspectConfess((Player) commandSender);

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