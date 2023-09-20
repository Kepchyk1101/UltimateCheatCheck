package me.kepchyk1101.ultimatecheatcheck.events;

import me.kepchyk1101.ultimatecheatcheck.cheatcheck.CheatCheck;
import org.bukkit.event.HandlerList;
import org.jetbrains.annotations.NotNull;

/**
 * en: The event is called when a suspect sent contacts to a moderator (/ucc contact <contacts>). May be canceled.
 * ру: Событие вызывается когда подозреваемый отправляет контакты модератору (/ucc contact <контакты>). Может быть отменено.
 */
public class CheatCheckContactEvent extends CancellableEvent {

    private final CheatCheck cheatCheck;
    private final String message;
    private static final HandlerList HANDLERS_LIST = new HandlerList();
    private boolean isCancelled;

    public CheatCheckContactEvent(CheatCheck cheatCheck, String message) {
        this.cheatCheck = cheatCheck;
        this.message = message;
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

    public String getMessage() {
        return message;
    }

}