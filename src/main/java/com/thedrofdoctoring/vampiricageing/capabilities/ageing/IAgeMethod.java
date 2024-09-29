package com.thedrofdoctoring.vampiricageing.capabilities.ageing;

public interface IAgeMethod {

    int[] getRankProgressions();
    String getId();
    IAgeType getValidType();
    boolean isEnabled();

    String getRemainingLang();

}
