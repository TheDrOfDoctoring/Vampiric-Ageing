package com.doctor.vampiricageing.init;

import com.doctor.vampiricageing.VampiricAgeing;
import com.doctor.vampiricageing.oils.SeniorityOil;
import de.teamlapen.vampirism.api.items.oil.IOil;
import de.teamlapen.vampirism.core.ModRegistries;
import net.minecraftforge.fml.RegistryObject;
import net.minecraftforge.registries.DeferredRegister;

public class ModOils {

    public static final DeferredRegister<IOil> OILS = DeferredRegister.create(ModRegistries.OILS, VampiricAgeing.MODID);
    public static final RegistryObject<SeniorityOil> SENIORITY_OIL = OILS.register("seniority_oil", () -> new SeniorityOil(0x3f0000, 8));


}
