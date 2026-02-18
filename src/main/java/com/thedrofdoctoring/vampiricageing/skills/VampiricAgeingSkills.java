package com.thedrofdoctoring.vampiricageing.skills;

import com.thedrofdoctoring.vampiricageing.VampiricAgeing;
import com.thedrofdoctoring.vampiricageing.actions.VampiricAgeingActions;
import de.teamlapen.vampirism.api.VampirismRegistries;
import de.teamlapen.vampirism.api.entity.player.hunter.IHunterPlayer;
import de.teamlapen.vampirism.api.entity.player.skills.ISkill;
import de.teamlapen.vampirism.api.entity.player.vampire.IVampirePlayer;
import de.teamlapen.vampirism.entity.player.hunter.skills.HunterSkills;
import de.teamlapen.vampirism.entity.player.skills.ActionSkill;
import de.teamlapen.vampirism.entity.player.skills.VampirismSkill;
import de.teamlapen.vampirism.entity.player.vampire.skills.VampireSkills;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;


public class VampiricAgeingSkills {

    public static final DeferredRegister<ISkill<?>> SKILLS = DeferredRegister.create(VampirismRegistries.Keys.SKILL, VampiricAgeing.MODID);
    public static final DeferredHolder<ISkill<?>, ISkill<IVampirePlayer>> CELERTIY_ACTION = SKILLS.register("celerity_skill", () -> new ActionSkill<>(VampiricAgeingActions.CELERITY_ACTION, VampireSkills.Trees.LEVEL, 0, false));
    public static final DeferredHolder<ISkill<?>, ISkill<IVampirePlayer>> BLOOD_DRAIN_SKILL = SKILLS.register("blood_drain_skill", () -> new ActionSkill<>(VampiricAgeingActions.DRAIN_BLOOD_ACTION, VampireSkills.Trees.LEVEL, 0, false));
    public static final DeferredHolder<ISkill<?>, ISkill<IVampirePlayer>> WATER_WALKING_SKILL = SKILLS.register("water_walking_skill", () -> new ActionSkill<>(VampiricAgeingActions.WATER_WALKING_ACTION, VampireSkills.Trees.LEVEL, 0, false));
    public static final DeferredHolder<ISkill<?>, ISkill<IVampirePlayer>> STEP_ASSIST_SKILL = SKILLS.register("step_assist_skill", () -> new ActionSkill<>(VampiricAgeingActions.STEP_ASSIST_ACTION, VampireSkills.Trees.LEVEL, 0, false));
    public static final DeferredHolder<ISkill<?>, ISkill<IHunterPlayer>> HUNTER_TELEPORT_SKILL = SKILLS.register("hunter_teleport_skill", () -> new ActionSkill<>(VampiricAgeingActions.HUNTER_TELEPORT_ACTION, HunterSkills.Trees.LEVEL, 0, false));
    public static final DeferredHolder<ISkill<?>, ISkill<IHunterPlayer>> LIMITED_BAT_MODE_SKILL = SKILLS.register("limited_bat_mode_skill", () -> new ActionSkill<>(VampiricAgeingActions.LIMITED_HUNTER_BATMODE_ACTION, HunterSkills.Trees.LEVEL, false));
    public static final DeferredHolder<ISkill<?>, ISkill<IHunterPlayer>> TAINTED_BLOOD_SKILL = SKILLS.register("tainted_blood_skill", () -> new VampirismSkill.SimpleHunterSkill(0, false));

    public static void register(IEventBus bus) {
        SKILLS.register(bus);
    }
}
