package com.doctor.vampiricageing.networking;

import com.doctor.vampiricageing.VampiricAgeing;
import com.doctor.vampiricageing.capabilities.AgeingCapability;
import com.doctor.vampiricageing.capabilities.IAgeingCapability;
import com.doctor.vampiricageing.capabilities.VampiricAgeingCapabilityManager;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.network.PacketBuffer;
import net.minecraftforge.fml.network.NetworkEvent;


import java.util.function.Supplier;

public class SyncCapabilityPacket {

    CompoundNBT tag;

    public SyncCapabilityPacket(PacketBuffer buf) {
        tag = buf.readNbt();
    }
    public SyncCapabilityPacket(CompoundNBT famCaps) {
        this.tag = famCaps;
    }

    public void toBytes(PacketBuffer buf) {
        buf.writeNbt(tag);
    }
    public void handle(Supplier<NetworkEvent.Context> ctx) {
        ctx.get().enqueueWork(() -> {
            PlayerEntity player = VampiricAgeing.proxy.getPlayer();
            IAgeingCapability agecap = VampiricAgeingCapabilityManager.getAge(player).orElse(new AgeingCapability());
            if (agecap != null) {
                agecap.deserializeNBT(tag);
                player.maxUpStep = agecap.getUpStep();
            }

        });
        ctx.get().setPacketHandled(true);
    }
}
