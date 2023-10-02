package com.doctor.vampiricageing.client.init;

import net.minecraft.client.Minecraft;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;

public class ClientRegistryHandler {
    public static void init(){
        MinecraftForge.EVENT_BUS.register(new AgeRankOverlay());
        MinecraftForge.EVENT_BUS.register(new ClientRenderHandler(Minecraft.getInstance()));

    }
}
