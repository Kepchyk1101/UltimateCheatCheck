package me.kepchyk1101.ultimatecheatcheck.cheatcheck;

import lombok.Getter;
import me.kepchyk1101.ultimatecheatcheck.UltimateCheatCheck;
import me.kepchyk1101.ultimatecheatcheck.managers.CheatCheckManager;
import me.kepchyk1101.ultimatecheatcheck.util.*;
import net.kyori.adventure.bossbar.BossBar;
import net.kyori.adventure.platform.bukkit.BukkitAudiences;
import net.kyori.adventure.text.Component;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.block.Block;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.ArrayList;
import java.util.UUID;

public class CheatCheck {

    private final UltimateCheatCheck plugin;
    private final RecoveryController recoveryController;
    private final BukkitAudiences audiences;

    @Getter private final Player suspect, moderator;
    @Getter private final UUID uuid;
    @Getter private final Location suspectLocation;
    @Getter private final Block blockUnderSuspect;
    @Getter private final Material blockTypeUnderSuspect;
    @Getter private final ArrayList<PotionEffectType> suspectEffects;
    @Getter private final int timer;

    @Getter private BossBar suspectBossBar, moderBossBar;
    @Getter private BukkitRunnable bossBarsController;
    @Getter private boolean paused;

    public CheatCheck(Player suspect, Player moderator) {
        this.plugin = UltimateCheatCheck.getInstance();
        this.recoveryController = plugin.getRecoveryController();
        this.audiences = plugin.getAudiences();
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

        suspectBossBar = getBossBarFromConfig("SuspectBossBar");
        moderBossBar = getBossBarFromConfig("ModerBossBar");

        bossBarsController = new BukkitRunnable() {

            int counter = timer;
            final float secondProgress = (float) (1.0 / timer);

            @Override
            public void run() {

                suspectBossBar.name(ChatUtils.getComponentFromText(ConfigUtils.getString("BossBars.SuspectBossBar.Text")
                        .replace("%timeLeft%", String.valueOf(counter))));
                moderBossBar.name(ChatUtils.getComponentFromText(ConfigUtils.getString("BossBars.ModerBossBar.Text")
                        .replace("%timeLeft%", String.valueOf(counter))
                        .replace("%suspect%", suspect.getName())));

                if (suspectBossBar.progress() >= secondProgress) {
                    suspectBossBar.progress(suspectBossBar.progress() - secondProgress);
                    moderBossBar.progress(moderBossBar.progress() - secondProgress);
                } else {
                    suspectBossBar.progress(0.0f);
                    moderBossBar.progress(0.0f);
                }

                if (counter-- < 1) {
                    CheatCheckManager.getInstance().timerExpired(suspect);
                }

            }

        };

        ChatUtils.sendTitle(
                suspect,
                ConfigUtils.getString("Titles.StartCheckSuspectTitle"),
                ConfigUtils.getString("Titles.StartCheckSuspectSubTitle"),
                0L, timer,0L);

        //suspect.teleport(BlockUtils.getCenteredBlockLocation(blockUnderSuspect).add(0, 0.5, 0));

        audiences.player(suspect).showBossBar(suspectBossBar);
        audiences.player(moderator).showBossBar(moderBossBar);

        bossBarsController.runTaskTimer(plugin, 0L, 20L);

        if (ConfigUtils.getBoolean("CheatCheck.AutoTeleportSuspect.enabled")) {
            suspect.teleport(ConfigUtils.getLocation("CheatCheck.AutoTeleportSuspect.to"));
        }

        if (ConfigUtils.getBoolean("CheatCheck.AutoTeleportModerToSuspect")) {
            moderator.teleport(suspect);
        }

        for (String effect : ConfigUtils.getStrings("CheatCheck.EffectsToSuspect")) {
            PotionEffectType potionEffectType = PotionEffectType.getByName(effect);
            if (potionEffectType != null) {
                suspect.addPotionEffect(new PotionEffect(potionEffectType, 1000000, 10));
                suspectEffects.add(potionEffectType);
            }
        }

        /*
         * The check is recorded in the recovery file so that in the event of
         * an emergency shutdown of the server, everything can be restored
         */
        String uuidString = uuid.toString();
        FileConfiguration recoveryConfig = recoveryController.getConfig();
        recoveryConfig.set("checks." + uuidString + ".suspect", suspect.getUniqueId().toString());
        recoveryController.saveConfig();

        paused = false;

    }

    public void stop() {

        for (PotionEffectType effect : suspectEffects) {
            suspect.removePotionEffect(effect);
        }

        plugin.getAudiences().player(suspect).clearTitle();
        suspect.teleport(suspectLocation);

        audiences.player(suspect).hideBossBar(suspectBossBar);
        audiences.player(moderator).hideBossBar(moderBossBar);

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

        suspectBossBar.name(ChatUtils.getComponentFromText(ConfigUtils.getString("BossBars.SuspectBossBar.PausedText")));
        moderBossBar.name(ChatUtils.getComponentFromText(ConfigUtils.getString("BossBars.ModerBossBar.PausedText")
                .replace("%suspect%", suspect.getName())));

        paused = true;

    }

    public void timerExpired() {

        bossBarsController.cancel();
        stop();

    }

    public void playSoundForSuspect(String path) {
        String soundName = ConfigUtils.getString(path);
        try {
            if (!soundName.equalsIgnoreCase("none")) {
                suspect.playSound(suspectLocation, Sound.valueOf(soundName), 1f, 1f);
            }
        } catch (IllegalArgumentException ignored) {
            plugin.getLogger().info("§6Failed to play sound: \"%sound%\". Check the plugin configuration".replace("%sound%", soundName));
        }
    }

    public BossBar getBossBarFromConfig(String path) {

        Component name = ChatUtils.getComponentFromText(ConfigUtils.getString("BossBars." + path + ".Text"));
        BossBar.Color color = BossBar.Color.valueOf(ConfigUtils.getString("BossBars." + path + ".Color"));
        BossBar.Overlay overlay = BossBarStyleAdapter.getKyoriBossBarStyle(ConfigUtils.getString("BossBars." + path + ".Style"));

        return BossBar.bossBar(name, 1f, color, overlay != null ? overlay : BossBar.Overlay.PROGRESS);

    }

}