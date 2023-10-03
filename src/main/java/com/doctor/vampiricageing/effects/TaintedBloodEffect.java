package com.doctor.vampiricageing.effects;

import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectCategory;

public class TaintedBloodEffect extends MobEffect {
    //This is essentially just a way for the player to track how much time they have left, no logic uses this effect.
    public TaintedBloodEffect(MobEffectCategory category, int colour) {
        super(category, colour);
    }
}
