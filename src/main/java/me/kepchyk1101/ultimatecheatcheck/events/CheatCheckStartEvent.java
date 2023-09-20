package me.kepchyk1101.ultimatecheatcheck.events;

import me.kepchyk1101.ultimatecheatcheck.cheatcheck.CheatCheck;
import org.bukkit.event.HandlerList;
import org.jetbrains.annotations.NotNull;

/**
 * en: Событие срабатывает до того, как подозреваемого вызовут на проверку. (/ucc start <player>). May be canceled.
 * ру: Событие вызывается перед вызовом игра на проверку (/ucc start <игрок>). Может быть отменено.
 */
public class CheatCheckStartEvent extends CancellableEvent {

    private final CheatCheck cheatCheck;
    private static final HandlerList HANDLERS_LIST = new HandlerList();
    private boolean isCancelled;

    public CheatCheckStartEvent(CheatCheck cheatCheck) {
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