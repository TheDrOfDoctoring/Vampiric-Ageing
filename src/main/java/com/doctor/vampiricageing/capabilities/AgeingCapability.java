package com.doctor.vampiricageing.capabilities;

import net.minecraft.nbt.CompoundTag;

public class AgeingCapability implements IAgeingCapability {

    private int age = 0;

    //Lost on either increase of Age or Death
    private int timeSinceAgeLoss;

    private int infectedSinceAgeLoss;
    private int drainedSinceAgeLoss;
    private int devouredSinceAgeLoss;
    private int huntedSinceAgeLoss;


    @Override
    public int getAge() {
        return age;
    }

    @Override
    public void setAge(int age) {
        this.timeSinceAgeLoss = 0;
        this.infectedSinceAgeLoss = 0;
        this.drainedSinceAgeLoss = 0;
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
    public int getHunted() {
        return huntedSinceAgeLoss;
    }

    @Override
    public void setHunted(int hunted) {
        this.huntedSinceAgeLoss = hunted;
    }

    @Override
    public int getInfected() {
        return infectedSinceAgeLoss;
    }



    @Override
    public void setInfected(int infected) {
        this.infectedSinceAgeLoss = infected;
    }
    @Override
    public int getDevoured() {
        return devouredSinceAgeLoss;
    }

    @Override
    public void setDevoured(int devoured) {
        this.devouredSinceAgeLoss = devoured;
    }
    @Override
    public int getDrained() {
        return drainedSinceAgeLoss;
    }

    @Override
    public void setDrained(int drained) {
        this.drainedSinceAgeLoss = drained;

    }
    public CompoundTag serializeNBT() {
        CompoundTag tag = new CompoundTag();
        tag.putInt("age", age);
        tag.putInt("time", timeSinceAgeLoss);
        tag.putInt("infected", infectedSinceAgeLoss);
        tag.putInt("drained", drainedSinceAgeLoss);
        tag.putInt("devoured", devouredSinceAgeLoss);
        tag.putInt("hunted", huntedSinceAgeLoss);
        return tag;
    }

    public void deserializeNBT(CompoundTag nbt) {
        this.age = nbt.getInt("age");
        this.devouredSinceAgeLoss = nbt.getInt("devoured");
        this.drainedSinceAgeLoss = nbt.getInt("drained");
        this.timeSinceAgeLoss = nbt.getInt("time");
        this.infectedSinceAgeLoss = nbt.getInt("infected");
        this.huntedSinceAgeLoss = nbt.getInt("hunted");
    }
}
