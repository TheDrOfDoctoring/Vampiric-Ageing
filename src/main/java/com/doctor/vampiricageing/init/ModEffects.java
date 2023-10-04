package com.doctor.vampiricageing.init;

import com.doctor.vampiricageing.VampiricAgeing;
import com.doctor.vampiricageing.effects.TaintedBloodEffect;
import net.minecraft.potion.Effect;
import net.minecraft.potion.EffectType;
import net.minecraftforge.fml.RegistryObject;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;

import java.awt.*;

public class ModEffects {

    public static final DeferredRegister<Effect> EFFECTS = DeferredRegister.create(ForgeRegistries.POTIONS, VampiricAgeing.MODID);

    public static final RegistryObject<TaintedBloodEffect> TAINTED_BLOOD_EFFECT = EFFECTS.register("tainted_blood_effect", () -> new TaintedBloodEffect(EffectType.HARMFUL, Color.MAGENTA.darker().darker().getRGB()));

    


}
