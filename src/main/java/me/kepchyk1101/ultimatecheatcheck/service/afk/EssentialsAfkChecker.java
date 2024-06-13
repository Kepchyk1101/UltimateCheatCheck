package me.kepchyk1101.ultimatecheatcheck.service.afk;

import com.earth2me.essentials.Essentials;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class EssentialsAfkChecker implements AfkChecker {

    @NotNull Essentials essentials;

    @Override
    public boolean isAfk(@NotNull Player player) {
        return essentials.getUser(player).isAfk();
    }

}
