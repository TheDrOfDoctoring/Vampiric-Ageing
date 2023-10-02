package com.doctor.vampiricageing.actions;

import com.doctor.vampiricageing.VampiricAgeing;
import de.teamlapen.vampirism.api.VampirismRegistries;
import de.teamlapen.vampirism.api.entity.player.actions.IAction;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.RegistryObject;

public class VampiricAgeingActions {
    public static final DeferredRegister<IAction<?>> ACTIONS = DeferredRegister.create(VampirismRegistries.ACTIONS_ID, VampiricAgeing.MODID);

    public static final RegistryObject<DrainBloodAction> DRAIN_BLOOD_ACTION = ACTIONS.register("drain_blood_action", DrainBloodAction::new);
    public static final RegistryObject<CelerityAction> CELERITY_ACTION = ACTIONS.register("celerity_action", CelerityAction::new);
    public static final RegistryObject<HunterTeleportAction> HUNTER_TELEPORT_ACTION = ACTIONS.register("hunter_teleport_action", HunterTeleportAction::new);
    public static final RegistryObject<LimitedHunterBatModeAction> LIMITED_HUNTER_BATMODE_ACTION = ACTIONS.register("limited_hunter_batmode_action", LimitedHunterBatModeAction::new);

    public static void register(IEventBus bus) {
        ACTIONS.register(bus);
    }
}
