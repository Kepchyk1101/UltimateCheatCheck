package com.kepchyk1101.ultimatecheatcheck.events;

import com.kepchyk1101.ultimatecheatcheck.cheatcheck.CheatCheck;
import org.bukkit.event.HandlerList;
import org.jetbrains.annotations.NotNull;

/**
 * en: The event is triggered before the player is acquitted (/ucc acquit <player>). May be canceled.
 * ру: Событие вызывается перед признанием игрока невиновным (/ucc acquit <игрок>). Может быть отменено.
 */
public class CheatCheckAcquitEvent extends CancellableEvent {

    private final CheatCheck cheatCheck;
    private static final HandlerList HANDLERS_LIST = new HandlerList();
    private boolean isCancelled;

    public CheatCheckAcquitEvent(CheatCheck cheatCheck) {
        this.cheatCheck = cheatCheck;
        this.isCancelled = false;
    }

    @Override
    public @NotNull HandlerList getHandlers() {
        return HANDLERS_LIST;
    }

    @Override
    public boolean isCancelled() {
        return isCancelled;
    }

    @Override
    public void setCancelled(boolean cancel) {
        this.isCancelled = cancel;
    }

    public CheatCheck getCheatCheck() {
        return cheatCheck;
    }

}