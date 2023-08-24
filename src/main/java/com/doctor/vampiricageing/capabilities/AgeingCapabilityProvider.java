package com.doctor.vampiricageing.capabilities;

import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.ICapabilityProvider;
import net.minecraftforge.common.util.INBTSerializable;
import net.minecraftforge.common.util.LazyOptional;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class AgeingCapabilityProvider implements ICapabilityProvider, INBTSerializable<CompoundTag> {
    private final IAgeingCapability ageing = new AgeingCapability();
    private final LazyOptional<IAgeingCapability> optional = LazyOptional.of(() -> ageing);

    @Override
    public @NotNull <T> LazyOptional<T> getCapability(@NotNull Capability<T> cap, @Nullable Direction side) {
        return VampiricAgeingCapabilityManager.AGEING_CAPABILITY.orEmpty(cap, this.optional);
    }


    @Override
    public CompoundTag serializeNBT() {
        return this.ageing.serializeNBT();
    }

    @Override
    public void deserializeNBT(CompoundTag nbt) {
       this.ageing.deserializeNBT(nbt);

    }
}
