package me.kepchyk1101.ultimatecheatcheck.events;

import me.kepchyk1101.ultimatecheatcheck.cheatcheck.CheatCheck;
import org.bukkit.event.HandlerList;
import org.jetbrains.annotations.NotNull;

/**
 * en: The event is triggered before the player is condemned (/ucc condemn <player>). May be canceled.
 * ру: Событие вызывается перед признанием игрока виновным (/ucc condemn <игрок>). Может быть отменено.
 */
public class CheatCheckCondemnEvent extends CancellableEvent {

    private final CheatCheck cheatCheck;
    private static final HandlerList HANDLER_LIST = new HandlerList();
    private boolean isCancelled;

    public CheatCheckCondemnEvent(CheatCheck cheatCheck) {
        this.cheatCheck = cheatCheck;
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