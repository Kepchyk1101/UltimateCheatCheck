package me.kepchyk1101.ultimatecheatcheck.cheatcheck;

import me.kepchyk1101.ultimatecheatcheck.UltimateCheatCheck;
import me.kepchyk1101.ultimatecheatcheck.managers.CheatCheckManager;
import me.kepchyk1101.ultimatecheatcheck.util.*;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.boss.BarColor;
import org.bukkit.boss.BarStyle;
import org.bukkit.boss.BossBar;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.util.BoundingBox;

import java.util.ArrayList;
import java.util.UUID;

public class CheatCheck {

    private final UltimateCheatCheck plugin;
    private final RecoveryController recoveryController;

    private final Player suspect, moderator;
    private final UUID uuid;
    private final Location suspectLocation;
    private final Block blockUnderSuspect;
    private final Material blockTypeUnderSuspect;
    private final ArrayList<PotionEffectType> suspectEffects;
    private final int timer;

    private BossBar suspectBossBar, moderBossBar;
    private BukkitRunnable bossBarsController;
    private boolean isPaused;

    public CheatCheck(Player suspect, Player moderator) {
        this.plugin = UltimateCheatCheck.getInstance();
        this.recoveryController = plugin.getRecoveryController();
        this.suspect = suspect;
        this.moderator = moderator;
        this.uuid = UUID.randomUUID();
        this.suspectLocation = suspect.getLocation();
        this.blockUnderSuspect = suspectLocation.getWorld().getBlockAt(suspectLocation.subtract(0, 1, 0));
        this.suspectLocation.add(0, 1, 0);
        this.blockTypeUnderSuspect = blockUnderSuspect.getType();
        this.suspectEffects = new ArrayList<>();
        this.timer = ConfigUtils.getInt("CheatCheck.Timer");
    }

    public void start() {

        suspectBossBar = Bukkit.createBossBar(
                ChatUtils.format(ConfigUtils.getString("BossBars.SuspectBossBar.Text"), suspect),
                BarColor.valueOf(ConfigUtils.getString("BossBars.SuspectBossBar.Color")),
                BarStyle.valueOf(ConfigUtils.getString("BossBars.SuspectBossBar.Style"))
        );

        moderBossBar = Bukkit.createBossBar(
                ChatUtils.format(ConfigUtils.getString("BossBars.ModerBossBar.Text"), moderator),
                BarColor.valueOf(ConfigUtils.getString("BossBars.ModerBossBar.Color")),
                BarStyle.valueOf(ConfigUtils.getString("BossBars.ModerBossBar.Style"))
        );

        bossBarsController = new BukkitRunnable() {

            int counter = timer;
            final double secondProgress = 1.0 / timer;

            @Override
            public void run() {

                // Bossbar timers

                suspectBossBar.setTitle(ChatUtils.format(
                        ConfigUtils.getString("BossBars.SuspectBossBar.Text")
                                .replace("%timeLeft%", String.valueOf(counter)),
                        suspect));
                moderBossBar.setTitle(ChatUtils.format(
                        ConfigUtils.getString("BossBars.ModerBossBar.Text")
                                .replace("%timeLeft%", String.valueOf(counter))
                                .replace("%suspect%", suspect.getName()),
                        moderator));

                if (suspectBossBar.getProgress() >= secondProgress) {
                    suspectBossBar.setProgress(suspectBossBar.getProgress() - secondProgress);
                    moderBossBar.setProgress(moderBossBar.getProgress() - secondProgress);
                } else {
                    suspectBossBar.setProgress(0.0D);
                    moderBossBar.setProgress(0.0D);
                }

                if (counter-- < 1) CheatCheckManager.getInstance().timerExpired(suspect);

            }

        };

        for (String effect : ConfigUtils.getStrings("CheatCheck.EffectsToSuspect")) {
            PotionEffectType potionEffectType = PotionEffectType.getByName(effect);
            if (potionEffectType != null) {
                suspect.addPotionEffect(new PotionEffect(potionEffectType, 1000000, 10));
                suspectEffects.add(potionEffectType);
            }
        }

        ChatUtils.sendTitle(
                suspect,
                ConfigUtils.getString("Titles.StartCheckSuspectTitle"),
                ConfigUtils.getString("Titles.StartCheckSuspectSubTitle"),
                0, 300 * 20,0);

        blockUnderSuspect.setType(Material.BEDROCK);

        suspect.teleport(BlockUtils.getCenteredBlockLocation(blockUnderSuspect).add(0, 0.5, 0));

        suspectBossBar.addPlayer(suspect);
        moderBossBar.addPlayer(moderator);

        bossBarsController.runTaskTimer(plugin, 0L, 20L);

        /*
         * The check is recorded in the recovery file so that in the event of
         * an emergency shutdown of the server, everything can be restored
         */
        String uuidString = uuid.toString();
        Location blockLocation = blockUnderSuspect.getLocation();

        FileConfiguration recoveryConfig = recoveryController.getConfig();
        recoveryConfig.set("checks." + uuidString + ".suspect", suspect.getUniqueId().toString());
        recoveryConfig.set("checks." + uuidString + ".block.x", blockLocation.getX());
        recoveryConfig.set("checks." + uuidString + ".block.y", blockLocation.getY());
        recoveryConfig.set("checks." + uuidString + ".block.z", blockLocation.getZ());
        recoveryConfig.set("checks." + uuidString + ".block.type", blockTypeUnderSuspect.toString());
        recoveryConfig.set("checks." + uuidString + ".block.world", blockLocation.getWorld().getUID().toString());
        recoveryController.saveConfig();

        isPaused = false;

    }

    public void stop() {

        for (PotionEffectType effect : suspectEffects)
            suspect.removePotionEffect(effect);

        suspect.resetTitle();
        blockUnderSuspect.setType(blockTypeUnderSuspect);
        suspect.teleport(suspectLocation);
        suspectBossBar.removeAll();
        moderBossBar.removeAll();
        bossBarsController.cancel();

        /*
         * Here the check record is deleted
         */
        FileConfiguration recoveryConfig = recoveryController.getConfig();
        recoveryConfig.set("checks." + uuid.toString(), null);
        recoveryController.saveConfig();

    }

    public void pause() {

        bossBarsController.cancel();

        suspectBossBar.setTitle(ChatUtils.format(ConfigUtils.getString("BossBars.SuspectBossBar.PausedText"), suspect));
        moderBossBar.setTitle(ChatUtils.format(ConfigUtils.getString("BossBars.ModerBossBar.PausedText")
                .replace("%suspect%", suspect.getName()), moderator));

        isPaused = true;

    }

    public void timerExpired() {

        bossBarsController.cancel();
        stop();

    }


    public Player getSuspect() {
        return suspect;
    }

    public boolean isPaused() {
        return isPaused;
    }

    public Player getModerator() {
        return moderator;
    }

    public BossBar getSuspectBossBar() {
        return suspectBossBar;
    }

    public BossBar getModerBossBar() {
        return moderBossBar;
    }

    public BukkitRunnable getBossBarsController() {
        return bossBarsController;
    }

    public Location getSuspectLocation() {
        return suspectLocation;
    }

    public Block getBlockUnderSuspect() {
        return blockUnderSuspect;
    }

    public ArrayList<PotionEffectType> getSuspectEffects() {
        return suspectEffects;
    }

    public Material getBlockTypeUnderSuspect() {
        return blockTypeUnderSuspect;
    }

    public UUID getUuid() {
        return uuid;
    }

    public int getTimer() {
        return timer;
    }

}