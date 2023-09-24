package com.doctor.vampiricageing.networking;

import net.minecraft.client.Minecraft;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.world.World;

public interface IProxy {
    void init();

    World getClientWorld();

    Minecraft getMinecraft();

    PlayerEntity getPlayer();
}
