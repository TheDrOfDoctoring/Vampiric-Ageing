package com.doctor.vampiricageing.skills;

import com.doctor.vampiricageing.VampiricAgeing;
import com.doctor.vampiricageing.actions.VampiricAgeingActions;
import de.teamlapen.vampirism.api.VampirismRegistries;
import de.teamlapen.vampirism.api.entity.player.hunter.IHunterPlayer;
import de.teamlapen.vampirism.api.entity.player.skills.ISkill;
import de.teamlapen.vampirism.api.entity.player.vampire.IVampirePlayer;
import de.teamlapen.vampirism.entity.player.skills.ActionSkill;
import de.teamlapen.vampirism.entity.player.skills.VampirismSkill;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.RegistryObject;

public class VampiricAgeingSkills {

    public static final DeferredRegister<ISkill<?>> SKILLS = DeferredRegister.create(VampirismRegistries.SKILLS_ID, VampiricAgeing.MODID);
    public static final RegistryObject<ISkill<IVampirePlayer>> CELERTIY_ACTION = SKILLS.register("celerity_skill", () -> new ActionSkill<>(VampiricAgeingActions.CELERITY_ACTION, 0, false));
    public static final RegistryObject<ISkill<IVampirePlayer>> BLOOD_DRAIN_SKILL = SKILLS.register("blood_drain_skill", () -> new ActionSkill<>(VampiricAgeingActions.DRAIN_BLOOD_ACTION, 0, false));
    public static final RegistryObject<ISkill<IVampirePlayer>> WATER_WALKING_SKILL = SKILLS.register("water_walking_skill", () -> new ActionSkill<>(VampiricAgeingActions.WATER_WALKING_ACTION, 0, false));
    public static final RegistryObject<ISkill<IHunterPlayer>> HUNTER_TELEPORT_SKILL = SKILLS.register("hunter_teleport_skill", () -> new ActionSkill<>(VampiricAgeingActions.HUNTER_TELEPORT_ACTION, 0, false));
    public static final RegistryObject<ISkill<IHunterPlayer>> LIMITED_BAT_MODE_SKILL = SKILLS.register("limited_bat_mode_skill", () -> new ActionSkill<>(VampiricAgeingActions.LIMITED_HUNTER_BATMODE_ACTION, 0, false));
    public static final RegistryObject<ISkill<IHunterPlayer>> TAINTED_BLOOD_SKILL = SKILLS.register("tainted_blood_skill", () -> new VampirismSkill.SimpleHunterSkill(0, false));

    public static void register(IEventBus bus) {
        SKILLS.register(bus);
    }
}
