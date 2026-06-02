package com.thedrofdoctoring.vampiricageing.actions;

import com.thedrofdoctoring.vampiricageing.VampiricAgeing;
import de.teamlapen.vampirism.api.VampirismRegistries;
import de.teamlapen.vampirism.api.entity.player.actions.IAction;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;


public class VampiricAgeingActions {
    public static final DeferredRegister<IAction<?>> ACTIONS = DeferredRegister.create(VampirismRegistries.Keys.ACTION, VampiricAgeing.MODID);

    public static final DeferredHolder<IAction<?>, DrainBloodAction> DRAIN_BLOOD_ACTION = ACTIONS.register("drain_blood_action", DrainBloodAction::new);
    public static final DeferredHolder<IAction<?>, CelerityAction> CELERITY_ACTION = ACTIONS.register("celerity_action", CelerityAction::new);
    public static final DeferredHolder<IAction<?>, HunterTeleportAction> HUNTER_TELEPORT_ACTION = ACTIONS.register("hunter_teleport_action", HunterTeleportAction::new);
    public static final DeferredHolder<IAction<?>, LimitedHunterBatModeAction> LIMITED_HUNTER_BATMODE_ACTION = ACTIONS.register("limited_hunter_batmode_action", LimitedHunterBatModeAction::new);
    public static final DeferredHolder<IAction<?>, WaterWalkingAction> WATER_WALKING_ACTION = ACTIONS.register("water_walking_action", WaterWalkingAction::new);
    public static final DeferredHolder<IAction<?>, StepAssistAction> STEP_ASSIST_ACTION = ACTIONS.register("step_assist_action", StepAssistAction::new);
    public static final DeferredHolder<IAction<?>, StepAssistHunterAction> STEP_ASSIST_HUNTER_ACTION = ACTIONS.register("step_assist_hunter_action", StepAssistHunterAction::new);
    public static final DeferredHolder<IAction<?>, HunterEyeAction> HUNTER_WISE_EYE = ACTIONS.register("hunter_wise_eye_action", HunterEyeAction::new);


    public static void register(IEventBus bus) {
        ACTIONS.register(bus);
    }
}
