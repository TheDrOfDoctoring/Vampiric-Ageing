package com.thedrofdoctoring.vampiricageing.capabilities.ageing.types;


import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import org.jetbrains.annotations.NotNull;

public abstract class TypeState {

    public abstract @NotNull CompoundTag serializeNBT(HolderLookup.@NotNull Provider provider, CompoundTag nbt);

    public abstract void deserializeNBT(HolderLookup.@NotNull Provider provider, @NotNull CompoundTag nbt);

    public abstract void deserializeUpdateNBT(HolderLookup.@NotNull Provider provider, @NotNull CompoundTag nbt);

    public abstract @NotNull CompoundTag serializeUpdateNBT(HolderLookup.@NotNull Provider provider, CompoundTag nbt);

}
