package com.kepchyk1101.ultimatecheatcheck.events;

import com.kepchyk1101.ultimatecheatcheck.cheatcheck.CheatCheck;
import org.bukkit.event.HandlerList;
import org.jetbrains.annotations.NotNull;

/**
 * en: The event is triggered before the pause for the check timer is set (/ucc pause <player>). May be canceled.
 * ру: Событие вызывается перед установкой паузы для таймера проверки (/ucc pause <игрок>). Может быть отменено.
 */
public class CheatCheckPauseEvent extends CancellableEvent {

    private final CheatCheck cheatCheck;
    private static final HandlerList HANDLERS_LIST = new HandlerList();
    private boolean isCancelled;

    public CheatCheckPauseEvent(CheatCheck cheatCheck) {
        this.cheatCheck = cheatCheck;
        this.isCancelled = false;
    }

    @Override
    public @NotNull HandlerList getHandlers() {
        return HANDLERS_LIST;
    }

    public CheatCheck getCheatCheck() {
        return cheatCheck;
    }

    @Override
    public boolean isCancelled() {
        return isCancelled;
    }

    @Override
    public void setCancelled(boolean cancel) {
        this.isCancelled = cancel;
    }

}