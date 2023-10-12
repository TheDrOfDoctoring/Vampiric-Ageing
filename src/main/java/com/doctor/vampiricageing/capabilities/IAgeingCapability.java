package com.doctor.vampiricageing.capabilities;

import net.minecraft.nbt.CompoundNBT;
import net.minecraftforge.common.util.INBTSerializable;

public interface IAgeingCapability extends INBTSerializable<CompoundNBT> {

    int getAge();

    void setAge(int age);

    int getTime();

    void setTime(int time);

    int getDevoured();

    void setDevoured(int devoured);


    int getInfected();

    void setInfected(int infected);

    void setDrained(int drained);

    int getDrained();
    void setHunted(int hunted);

    int getHunted();
    int getTemporaryTaintedAgeBonus();

    void setTemporaryTaintedAgeBonus(int bonus);
    int getTemporaryTainedTicks();

    void setTemporaryTainedTicks(int ticks);

    boolean getBatMode();

    void setBatMode(boolean batMode);
    int getTicksInSun();

    void setTicksInSun(int ticks);
    boolean getUpStep();

    void setUpStep(boolean upStepSetter);

    int getAgeSkills();

    void setAgeSkills(int skills);

}
