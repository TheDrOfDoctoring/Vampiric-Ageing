package com.doctor.vampiricageing.networking;

import com.doctor.vampiricageing.VampiricAgeing;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.network.NetworkRegistry;
import net.minecraftforge.network.simple.SimpleChannel;

public class Networking {
    public static SimpleChannel INSTANCE;

    public static void registerMessages() {
        INSTANCE = NetworkRegistry.newSimpleChannel(new ResourceLocation(VampiricAgeing.MODID, "network"), () -> "1.0", s -> true, s -> true);

        INSTANCE.registerMessage(0,
                SyncCapabilityPacket.class,
                SyncCapabilityPacket::toBytes,
                SyncCapabilityPacket::new,
                SyncCapabilityPacket::handle);
    }
}
