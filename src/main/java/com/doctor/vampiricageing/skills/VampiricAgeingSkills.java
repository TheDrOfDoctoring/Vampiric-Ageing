package com.doctor.vampiricageing.skills;

import com.doctor.vampiricageing.VampiricAgeing;
import com.doctor.vampiricageing.actions.VampiricAgeingActions;
import de.teamlapen.vampirism.api.VampirismRegistries;
import de.teamlapen.vampirism.api.entity.player.skills.ISkill;
import de.teamlapen.vampirism.api.entity.player.vampire.IVampirePlayer;
import de.teamlapen.vampirism.entity.player.skills.ActionSkill;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.RegistryObject;

public class VampiricAgeingSkills {

    public static final DeferredRegister<ISkill<?>> SKILLS = DeferredRegister.create(VampirismRegistries.SKILLS_ID, VampiricAgeing.MODID);
    public static final RegistryObject<ISkill<IVampirePlayer>> CELERTIY_ACTION = SKILLS.register("celerity_skill", () -> new ActionSkill<>(VampiricAgeingActions.CELERITY_ACTION, false));
    public static final RegistryObject<ISkill<IVampirePlayer>> BLOOD_DRAIN_SKILL = SKILLS.register("blood_drain_skill", () -> new ActionSkill<>(VampiricAgeingActions.DRAIN_BLOOD_ACTION, false));

    public static void register(IEventBus bus) {
        SKILLS.register(bus);
    }
}
