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
public class ContactSubCommand implements SubCommand {

    @NotNull
    CheckService checkService;

    @Override
    public boolean onSubCommand(@NotNull CommandSender commandSender, @NotNull String[] args) {

        checkService.playerContact((Player) commandSender, String.join(" ", args));

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