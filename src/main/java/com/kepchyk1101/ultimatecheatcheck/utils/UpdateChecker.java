package com.kepchyk1101.ultimatecheatcheck.utils;

import com.kepchyk1101.ultimatecheatcheck.UltimateCheatCheck;
import org.bukkit.Bukkit;
import org.bukkit.event.Listener;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.Scanner;
import java.util.function.Consumer;

public class UpdateChecker implements Listener {

    private final UltimateCheatCheck plugin;
    private final int resourceId;

    public UpdateChecker(UltimateCheatCheck plugin, int resourceId) {
        this.plugin = plugin;
        this.resourceId = resourceId;
    }

    public void getLatestVersion(Consumer<String> consumer) {

        Bukkit.getScheduler().runTaskAsynchronously(plugin, () -> {

            try (InputStream is = new URL("https://api.spigotmc.org/legacy/update.php?resource=" + this.resourceId).openStream(); Scanner scan = new Scanner(is)) {
                if (scan.hasNext()) {
                    consumer.accept(scan.next());
                }
            } catch (IOException e) {
                this.plugin.getLogger().warning(ConfigUtils.getMessage("system.checkUpdateFailed"));
            }

        });

    }

}
