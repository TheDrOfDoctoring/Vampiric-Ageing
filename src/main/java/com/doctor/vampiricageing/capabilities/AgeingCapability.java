package com.doctor.vampiricageing.capabilities;

import net.minecraft.nbt.CompoundNBT;

public class AgeingCapability implements IAgeingCapability {

    private int age = 0;

    //Lost on either increase of Age or Death
    private int timeSinceAgeLoss;

    private int infectedSinceAgeLoss;
    private int drainedSinceAgeLoss;
    private int devouredSinceAgeLoss;
    private int huntedSinceAgeLoss;
    private boolean upStepChange;


    @Override
    public int getAge() {
        return age;
    }

    @Override
    public void setAge(int age) {
        this.timeSinceAgeLoss = 0;
        this.infectedSinceAgeLoss = 0;
        this.drainedSinceAgeLoss = 0;
        this.devouredSinceAgeLoss = 0;
        this.huntedSinceAgeLoss = 0;
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
    public boolean getUpStep() {
        return upStepChange;
    }

    @Override
    public void setUpStep(boolean upStepSetter) {
        this.upStepChange = upStepSetter;
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
    public CompoundNBT serializeNBT() {
        CompoundNBT tag = new CompoundNBT();
        tag.putInt("age", age);
        tag.putInt("time", timeSinceAgeLoss);
        tag.putInt("infected", infectedSinceAgeLoss);
        tag.putInt("drained", drainedSinceAgeLoss);
        tag.putInt("devoured", devouredSinceAgeLoss);
        tag.putInt("hunted", huntedSinceAgeLoss);
        tag.putBoolean("upstep", upStepChange);
        return tag;
    }

    public void deserializeNBT(CompoundNBT nbt) {
        this.age = nbt.getInt("age");
        this.devouredSinceAgeLoss = nbt.getInt("devoured");
        this.drainedSinceAgeLoss = nbt.getInt("drained");
        this.timeSinceAgeLoss = nbt.getInt("time");
        this.infectedSinceAgeLoss = nbt.getInt("infected");
        this.huntedSinceAgeLoss = nbt.getInt("hunted");
        this.upStepChange = nbt.getBoolean("upstep");
    }
}
