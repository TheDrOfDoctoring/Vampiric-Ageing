package com.doctor.vampiricageing.capabilities;

import net.minecraft.nbt.CompoundTag;

public class AgeingCapability implements IAgeingCapability {

    private int age = 0;

    //Lost on either increase of Age or Death
    private int timeSinceAgeLoss;

    private int infectedSinceAgeLoss;


    @Override
    public int getAge() {
        return age;
    }

    @Override
    public void setAge(int age) {
        this.timeSinceAgeLoss = 0;
        this.infectedSinceAgeLoss = 0;
        this.age = age;

    }

    @Override
    public int getTime() {
        return timeSinceAgeLoss;
    }

    @Override
    public void setTime(int time) {
        this.timeSinceAgeLoss = time;
    }

    @Override
    public int getInfected() {
        return infectedSinceAgeLoss;
    }

    @Override
    public void setInfected(int infected) {
        this.infectedSinceAgeLoss = infected;

    }
    public CompoundTag serializeNBT() {
        CompoundTag tag = new CompoundTag();
        tag.putInt("age", age);
        tag.putInt("time", timeSinceAgeLoss);
        tag.putInt("infected", infectedSinceAgeLoss);
        return tag;
    }

    public void deserializeNBT(CompoundTag nbt) {
        this.age = nbt.getInt("age");
        this.timeSinceAgeLoss = nbt.getInt("time");
        this.infectedSinceAgeLoss = nbt.getInt("infected");
    }
}
