package com.doctor.vampiricageing.networking;

import net.minecraft.client.Minecraft;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.world.World;

public class ServerProxy implements IProxy {

    @Override
    public void init() {
    }

    @Override
    public World getClientWorld() {
       return null;
    }

    @Override
    public Minecraft getMinecraft() {
       return null;
    }

    @Override
    public PlayerEntity getPlayer() {
       return null;
    }
}
