package com.doctor.vampiricageing.capabilities;

import net.minecraft.nbt.CompoundNBT;
import net.minecraft.util.Direction;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.ICapabilityProvider;
import net.minecraftforge.common.util.INBTSerializable;
import net.minecraftforge.common.util.LazyOptional;

import javax.annotation.Nullable;

public class AgeingCapabilityProvider implements ICapabilityProvider, INBTSerializable<CompoundNBT> {
    private final IAgeingCapability ageing = new AgeingCapability();
    private final LazyOptional<IAgeingCapability> optional = LazyOptional.of(() -> ageing);

    @Override
    public <T> LazyOptional<T> getCapability(Capability<T> cap, @Nullable Direction side) {
        return VampiricAgeingCapabilityManager.AGEING_CAPABILITY.orEmpty(cap, this.optional);
    }


    @Override
    public CompoundNBT serializeNBT() {
        return this.ageing.serializeNBT();
    }

    @Override
    public void deserializeNBT(CompoundNBT nbt) {
       this.ageing.deserializeNBT(nbt);

    }
}
