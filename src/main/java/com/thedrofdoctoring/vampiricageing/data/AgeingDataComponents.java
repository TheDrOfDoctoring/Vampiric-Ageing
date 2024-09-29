package com.thedrofdoctoring.vampiricageing.data;

import com.mojang.serialization.Codec;
import com.thedrofdoctoring.vampiricageing.VampiricAgeing;
import net.minecraft.core.component.DataComponentType;
import net.minecraft.network.codec.ByteBufCodecs;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;

public class AgeingDataComponents {

    public static final DeferredRegister.DataComponents COMPONENTS = DeferredRegister.createDataComponents(VampiricAgeing.MODID);

    public static final DeferredHolder<DataComponentType<?>, DataComponentType<Integer>> AGE_RANK =
            COMPONENTS.registerComponentType("age_rank", builder -> builder.persistent(Codec.INT).networkSynchronized(ByteBufCodecs.INT));

    public static void register(IEventBus bus) {
        COMPONENTS.register(bus);
    }
}
