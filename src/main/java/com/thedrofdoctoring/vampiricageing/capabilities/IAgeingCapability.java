package com.thedrofdoctoring.vampiricageing.capabilities;


import com.thedrofdoctoring.vampiricageing.capabilities.ageing.IAgeMethod;
import com.thedrofdoctoring.vampiricageing.capabilities.ageing.IAgeType;

public interface IAgeingCapability  {

    int getAge();

    void setAge(int age);

    void setRankProgress(int progress);
    int getRankProgress();

    int getTemporaryTaintedAgeBonus();

    void setTemporaryTaintedAgeBonus(int bonus);
    int getTemporaryTainedTicks();

    void setTemporaryTaintedTicks(int ticks);

    IAgeMethod getMethod();
    IAgeType getType();
    void setMethod(IAgeMethod method);
    void setType(IAgeType type);

    boolean isTransformed();

    void setTransformed(boolean transformed);


}
