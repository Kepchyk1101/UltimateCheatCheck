package me.kepchyk1101.ultimatecheatcheck.util;

import net.kyori.adventure.bossbar.BossBar;

public class BossBarStyleAdapter {

    public static BossBar.Overlay getKyoriBossBarStyle(String minecraftBossBarStyle) {

        switch (minecraftBossBarStyle) {

            case "SOLID":
                return BossBar.Overlay.PROGRESS;
            case "SEGMENTED_6":
                return BossBar.Overlay.NOTCHED_6;
            case "SEGMENTED_10":
                return BossBar.Overlay.NOTCHED_10;
            case "SEGMENTED_12":
                return BossBar.Overlay.NOTCHED_12;
            case "SEGMENTED_20":
                return BossBar.Overlay.NOTCHED_20;
            default:
                return null;

        }

    }

}
