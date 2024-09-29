package com.thedrofdoctoring.vampiricageing.capabilities.ageing.methods;

import com.thedrofdoctoring.vampiricageing.capabilities.ageing.IAgeMethod;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;

public abstract class HuntingMethod implements IAgeMethod {



    public abstract void onAgedKill(LivingEntity target, Player sourceKiller);


}
