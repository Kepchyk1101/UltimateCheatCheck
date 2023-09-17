package com.kepchyk1101.ultimatecheatcheck.events;

import com.kepchyk1101.ultimatecheatcheck.cheatcheck.CheatCheck;
import org.bukkit.event.HandlerList;
import org.jetbrains.annotations.NotNull;

/**
 * en: The event is triggered when a suspect voluntarily admits to using cheats (/ucc confess). May be canceled.
 * ру: Событие вызывается когда подозреваемый самовольно признается в использовании стороннего ПО (/ucc confess). Может быть отменено.
 */
public class CheatCheckConfessEvent extends CancellableEvent {

    private final CheatCheck cheatCheck;
    private static final HandlerList HANDLER_LIST = new HandlerList();
    private boolean isCancelled;

    public CheatCheckConfessEvent(CheatCheck cheatCheck) {
        this.cheatCheck = cheatCheck;
        isCancelled = false;
    }

    @Override
    public boolean isCancelled() {
        return isCancelled;
    }

    @Override
    public void setCancelled(boolean cancel) {
        this.isCancelled = cancel;
    }

    @Override
    public @NotNull HandlerList getHandlers() {
        return HANDLER_LIST;
    }

    public CheatCheck getCheatCheck() {
        return cheatCheck;
    }

}