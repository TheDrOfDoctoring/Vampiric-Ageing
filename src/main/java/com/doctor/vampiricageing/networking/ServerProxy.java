package com.doctor.vampiricageing.networking;

import net.minecraft.client.Minecraft;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;

public class ServerProxy implements IProxy {

    @Override
    public void init() {
    }

    @Override
    public Level getClientWorld() {
       return null;
    }

    @Override
    public Minecraft getMinecraft() {
       return null;
    }

    @Override
    public Player getPlayer() {
       return null;
    }
}
