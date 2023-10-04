package com.doctor.vampiricageing.skills;

import com.doctor.vampiricageing.VampiricAgeing;
import com.doctor.vampiricageing.actions.VampiricAgeingActions;
import de.teamlapen.vampirism.api.entity.player.hunter.IHunterPlayer;
import de.teamlapen.vampirism.api.entity.player.skills.ISkill;
import de.teamlapen.vampirism.core.ModRegistries;
import de.teamlapen.vampirism.player.skills.ActionSkill;
import de.teamlapen.vampirism.player.skills.VampirismSkill;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.RegistryObject;
import net.minecraftforge.registries.DeferredRegister;

public class VampiricAgeingSkills {

    public static final DeferredRegister<ISkill> SKILLS = DeferredRegister.create(ModRegistries.SKILLS, VampiricAgeing.MODID);
    public static final RegistryObject<ISkill> CELERTIY_ACTION = SKILLS.register("celerity_skill", () -> new ActionSkill<>(VampiricAgeingActions.CELERITY_ACTION.get(), false));
    public static final RegistryObject<ISkill> BLOOD_DRAIN_SKILL = SKILLS.register("blood_drain_skill", () -> new ActionSkill<>(VampiricAgeingActions.DRAIN_BLOOD_ACTION.get(), false));
    public static final RegistryObject<ISkill> HUNTER_TELEPORT_SKILL = SKILLS.register("hunter_teleport_skill", () -> new ActionSkill<>(VampiricAgeingActions.HUNTER_TELEPORT_ACTION.get(), false));
    public static final RegistryObject<ISkill> LIMITED_BAT_MODE_SKILL = SKILLS.register("limited_bat_mode_skill", () -> new ActionSkill<>(VampiricAgeingActions.LIMITED_HUNTER_BATMODE_ACTION.get(), false));
    public static final RegistryObject<ISkill> TAINTED_BLOOD_SKILL = SKILLS.register("tainted_blood_skill", () -> new VampirismSkill.SimpleHunterSkill(false));
    public static void register(IEventBus bus) {
        SKILLS.register(bus);
    }
}
