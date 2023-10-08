package me.kepchyk1101.ultimatecheatcheck.util;

import org.bukkit.Location;
import org.bukkit.block.Block;

public class BlockUtils {

    public static Location getCenteredBlockLocation(Block block) {

        int minX = block.getX();
        int maxX = minX + 1;
        int minY = block.getY();
        int maxY = minY + 1;
        int minZ = block.getZ();
        int maxZ = minZ + 1;

        double centerX = minX + (maxX - minX) * 0.5D;
        double centerY = minY + (maxY - minY) * 0.5D;
        double centerZ = minZ + (maxZ - minZ) * 0.5D;

        return new Location(block.getWorld(), centerX, centerY, centerZ);

    }

}