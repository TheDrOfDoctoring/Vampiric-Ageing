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
    private int temporaryTaintedAgeBonus;
    private int temporaryTaintedTicks;
    private boolean batMode;
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
        this.huntedSinceAgeLoss = 0;
        this.devouredSinceAgeLoss = 0;
        this.temporaryTaintedAgeBonus = 0;
        this.temporaryTaintedTicks = 0;
        this.batMode = false;
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
    @Override
    public int getTemporaryTaintedAgeBonus() {
        return temporaryTaintedAgeBonus;
    }

    @Override
    public void setTemporaryTaintedAgeBonus(int bonus) {
        this.temporaryTaintedAgeBonus = bonus;
    }

    @Override
    public int getTemporaryTainedTicks() {
        return this.temporaryTaintedTicks;
    }

    @Override
    public void setTemporaryTainedTicks(int ticks) {
        this.temporaryTaintedTicks = ticks;

    }

    @Override
    public boolean getBatMode() {
        return this.batMode;
    }

    @Override
    public void setBatMode(boolean batMode) {
        this.batMode = batMode;
    }

    @Override
    public boolean getUpStep() {
        return upStepChange;
    }

    @Override
    public void setUpStep(boolean upStepSetter) {
        this.upStepChange = upStepSetter;
    }

    public CompoundNBT serializeNBT() {
        CompoundNBT tag = new CompoundNBT();
        tag.putInt("age", age);
        tag.putInt("time", timeSinceAgeLoss);
        tag.putInt("infected", infectedSinceAgeLoss);
        tag.putInt("drained", drainedSinceAgeLoss);
        tag.putInt("devoured", devouredSinceAgeLoss);
        tag.putInt("hunted", huntedSinceAgeLoss);
        tag.putInt("taintedTicks", temporaryTaintedTicks);
        tag.putInt("taintedBonus", temporaryTaintedAgeBonus);
        tag.putBoolean("batMode", batMode);
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
        this.temporaryTaintedTicks = nbt.getInt("taintedTicks");
        this.temporaryTaintedAgeBonus = nbt.getInt("taintedBonus");
        this.upStepChange = nbt.getBoolean("upstep");
        this.batMode = nbt.getBoolean("batMode");
    }
}
