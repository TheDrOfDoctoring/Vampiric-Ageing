package com.thedrofdoctoring.vampiricageing.capabilities.cache;

import net.minecraft.world.entity.player.Player;
import org.jetbrains.annotations.NotNull;

// TODO: Move the special attribute mixin fields to this cache
public class AgeingPlayerCache {

    public static AgeingPlayerCache get(@NotNull Player player) {
        return ((IAgeingPlayerCache) player).ageing$getCache();
    }

    public boolean hasBypassInvisibility;
}
