package com.thedrofdoctoring.vampiricageing.init;

import com.thedrofdoctoring.vampiricageing.VampiricAgeing;
import com.thedrofdoctoring.vampiricageing.oils.SeniorityOil;
import de.teamlapen.vampirism.api.VampirismRegistries;
import de.teamlapen.vampirism.api.items.oil.IOil;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;


public class ModOils {

    public static final DeferredRegister<IOil> OILS = DeferredRegister.create(VampirismRegistries.Keys.OIL, VampiricAgeing.MODID);
    public static final DeferredHolder<IOil, SeniorityOil> SENIORITY_OIL = OILS.register("seniority_oil", () -> new SeniorityOil(0x3f0000, 8));


}
