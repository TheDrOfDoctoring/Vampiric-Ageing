package com.doctor.vampiricageing.capabilities;

import de.teamlapen.lib.lib.network.ISyncable;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.common.capabilities.AutoRegisterCapability;
import net.minecraftforge.common.util.INBTSerializable;
import org.jetbrains.annotations.NotNull;

public interface IAgeingCapability extends INBTSerializable<CompoundTag> {

    int getAge();

    void setAge(int age);

    int getTime();

    void setTime(int time);

    int getInfected();

    void setInfected(int infected);

    void setDrained(int drained);

    int getDrained();

}
