package com.thedrofdoctoring.vampiricageing.capabilities;


import com.thedrofdoctoring.vampiricageing.capabilities.ageing.IAgeMethod;
import com.thedrofdoctoring.vampiricageing.capabilities.ageing.IAgeType;
import com.thedrofdoctoring.vampiricageing.capabilities.ageing.types.TypeState;

public interface IAgeingCapability  {

    int getAge();

    void setAge(int age);

    void setRankProgress(int progress);
    int getRankProgress();

    IAgeMethod getMethod();
    IAgeType getType();
    void setMethod(IAgeMethod method);
    void setType(IAgeType type);

    TypeState getTypeState();



}
