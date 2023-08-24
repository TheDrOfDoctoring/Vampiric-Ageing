package com.doctor.vampiricageing.networking;

import com.doctor.vampiricageing.VampiricAgeing;
import com.doctor.vampiricageing.capabilities.AgeingCapability;
import com.doctor.vampiricageing.capabilities.IAgeingCapability;
import com.doctor.vampiricageing.capabilities.VampiricAgeingCapabilityManager;
import net.minecraft.client.Minecraft;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.entity.player.Player;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.network.NetworkEvent;

import java.util.function.Supplier;

public class SyncCapabilityPacket {

    CompoundTag tag;

    public SyncCapabilityPacket(FriendlyByteBuf buf) {
        tag = buf.readNbt();
    }
    public SyncCapabilityPacket(CompoundTag famCaps) {
        this.tag = famCaps;
    }

    public void toBytes(FriendlyByteBuf buf) {
        buf.writeNbt(tag);
    }
    public void handle(Supplier<NetworkEvent.Context> ctx) {
        ctx.get().enqueueWork(() -> {
            Player player = VampiricAgeing.proxy.getPlayer();
            IAgeingCapability agecap = VampiricAgeingCapabilityManager.getAge(player).orElse(new AgeingCapability());
            if (agecap != null) {
                agecap.deserializeNBT(tag);
            }
        });
        ctx.get().setPacketHandled(true);
    }
}
