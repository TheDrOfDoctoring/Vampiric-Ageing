package com.thedrofdoctoring.vampiricageing.capabilities.ageing;

import java.util.HashSet;
import java.util.Set;

public class AgeingRegistry {

    private static final HashSet<IAgeMethod> methods = new HashSet<>();
    private static final HashSet<IAgeType> types = new HashSet<>();

    public static void registerAgeMethod(IAgeMethod method) {
        methods.add(method);
    }
    public static Set<IAgeMethod> getAgeingMethods() {
        return Set.copyOf(methods);
    }
    public static void registerAgeType(IAgeType type) {
        types.add(type);
    }
    public static Set<IAgeType> getAgeingTypes() {
        return Set.copyOf(types);
    }
}
