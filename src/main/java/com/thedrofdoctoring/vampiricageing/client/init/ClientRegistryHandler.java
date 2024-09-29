package com.thedrofdoctoring.vampiricageing.client.init;

import net.minecraft.client.Minecraft;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.common.NeoForge;


public class ClientRegistryHandler {
    public static void init(IEventBus modbus){
        modbus.addListener(VampiricAgeingOverlays::registerOverlays);
        ClientRenderHandler renderHandler = new ClientRenderHandler(Minecraft.getInstance());
        NeoForge.EVENT_BUS.register(renderHandler);


    }
}
