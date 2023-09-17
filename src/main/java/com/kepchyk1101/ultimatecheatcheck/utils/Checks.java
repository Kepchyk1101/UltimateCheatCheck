package com.kepchyk1101.ultimatecheatcheck.utils;

import com.kepchyk1101.ultimatecheatcheck.UltimateCheatCheck;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.File;
import java.io.IOException;

public class Checks {

    private final File file;
    private final FileConfiguration config;
    private final boolean exists;

    public Checks(File file) {

        this.file = file;

        if (!file.exists()) {
            UltimateCheatCheck.getInstance().saveResource("checks.yml", false);
            exists = false;
        } else
            exists = true;

        config = YamlConfiguration.loadConfiguration(file);

    }

    public void save() {
        try {
            config.save(file);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public FileConfiguration getConfig() {
        return config;
    }

    public boolean isExists() {
        return exists;
    }

}