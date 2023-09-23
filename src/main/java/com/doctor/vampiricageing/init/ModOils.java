package com.doctor.vampiricageing.init;

import com.doctor.vampiricageing.VampiricAgeing;
import com.doctor.vampiricageing.oils.SeniorityOil;
import de.teamlapen.vampirism.api.VampirismRegistries;
import de.teamlapen.vampirism.api.items.oil.IOil;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.RegistryObject;

public class ModOils {

    public static final DeferredRegister<IOil> OILS = DeferredRegister.create(VampirismRegistries.OILS_ID, VampiricAgeing.MODID);
    public static final RegistryObject<SeniorityOil> SENIORITY_OIL = OILS.register("seniority_oil", () -> new SeniorityOil(0x3f0000, 8));


}
