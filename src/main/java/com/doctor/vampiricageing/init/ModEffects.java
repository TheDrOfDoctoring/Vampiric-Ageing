package com.doctor.vampiricageing.init;

import com.doctor.vampiricageing.VampiricAgeing;
import com.doctor.vampiricageing.effects.TaintedBloodEffect;
import de.teamlapen.lib.util.Color;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectCategory;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

public class ModEffects {

    public static final DeferredRegister<MobEffect> EFFECTS = DeferredRegister.create(ForgeRegistries.MOB_EFFECTS, VampiricAgeing.MODID);

    public static final RegistryObject<TaintedBloodEffect> TAINTED_BLOOD_EFFECT = EFFECTS.register("tainted_blood_effect", () -> new TaintedBloodEffect(MobEffectCategory.HARMFUL, Color.MAGENTA_DARK.getRGB()));

    


}
