package me.kepchyk1101.ultimatecheatcheck.cheatcheck;

import org.bukkit.entity.Player;

import java.io.Serializable;

public interface ICheatCheck extends Serializable {

    void start();

    void stop();

    void timerExpired();

    Player getSuspect();

    Player getModer();

}
