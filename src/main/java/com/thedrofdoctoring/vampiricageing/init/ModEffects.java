package com.thedrofdoctoring.vampiricageing.init;

import com.thedrofdoctoring.vampiricageing.VampiricAgeing;
import com.thedrofdoctoring.vampiricageing.effects.TaintedBloodEffect;
import de.teamlapen.lib.util.Color;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectCategory;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;


public class ModEffects {

    public static final DeferredRegister<MobEffect> EFFECTS = DeferredRegister.create(Registries.MOB_EFFECT, VampiricAgeing.MODID);

    public static final DeferredHolder<MobEffect, TaintedBloodEffect> TAINTED_BLOOD_EFFECT = EFFECTS.register("tainted_blood_effect", () -> new TaintedBloodEffect(MobEffectCategory.HARMFUL, Color.MAGENTA_DARK.getRGB()));

    


}
