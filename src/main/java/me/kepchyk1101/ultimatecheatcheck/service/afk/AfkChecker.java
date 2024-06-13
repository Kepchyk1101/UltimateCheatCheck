package me.kepchyk1101.ultimatecheatcheck.service.afk;

import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

public interface AfkChecker {

    boolean isAfk(@NotNull Player player);

}
