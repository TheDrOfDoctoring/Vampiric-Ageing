package com.doctor.vampiricageing.effects;

import net.minecraft.potion.Effect;
import net.minecraft.potion.EffectType;


public class TaintedBloodEffect extends Effect {
    //This is essentially just a way for the player to track how much time they have left, no logic uses this effect.
    public TaintedBloodEffect(EffectType category, int colour) {
        super(category, colour);
    }
}
