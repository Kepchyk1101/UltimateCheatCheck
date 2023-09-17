package com.kepchyk1101.ultimatecheatcheck.utils;

import com.kepchyk1101.ultimatecheatcheck.events.CancellableEvent;
import org.bukkit.Bukkit;

public class EventUtils {

    public static boolean callAndCheckEvent(CancellableEvent event) {
        Bukkit.getPluginManager().callEvent(event);
        return event.isCancelled();
    }

}