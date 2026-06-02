package com.thedrofdoctoring.vampiricageing.skills;

import com.thedrofdoctoring.vampiricageing.VampiricAgeing;
import com.thedrofdoctoring.vampiricageing.actions.WerewolfEyeAction;
import de.teamlapen.vampirism.api.VampirismRegistries;
import de.teamlapen.vampirism.api.entity.player.actions.IAction;
import de.teamlapen.vampirism.api.entity.player.skills.ISkill;
import de.teamlapen.vampirism.entity.player.skills.ActionSkill;
import de.teamlapen.werewolves.api.entities.player.IWerewolfPlayer;
import de.teamlapen.werewolves.core.ModSkills;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;

public class WerewolfAgeingSkills {

    public static final DeferredRegister<IAction<?>> ACTIONS = DeferredRegister.create(VampirismRegistries.Keys.ACTION, VampiricAgeing.MODID);
    public static final DeferredHolder<IAction<?>, WerewolfEyeAction> IMPROVED_SENSES_ACTION = ACTIONS.register("improved_senses_action", WerewolfEyeAction::new);

    public static final DeferredRegister<ISkill<?>> SKILLS = DeferredRegister.create(VampirismRegistries.Keys.SKILL, VampiricAgeing.MODID);
    public static final DeferredHolder<ISkill<?>, ISkill<IWerewolfPlayer>> IMPORVED_SENSES_SKILL = SKILLS.register("improved_senses_skill", () -> new ActionSkill<>(IMPROVED_SENSES_ACTION, ModSkills.Trees.LEVEL, 0, false));


    public static void register(IEventBus bus) {
        SKILLS.register(bus);
        ACTIONS.register(bus);
    }
}
