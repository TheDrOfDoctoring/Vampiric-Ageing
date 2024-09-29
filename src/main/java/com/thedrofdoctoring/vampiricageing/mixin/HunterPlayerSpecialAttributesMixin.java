package com.thedrofdoctoring.vampiricageing.mixin;

import com.thedrofdoctoring.vampiricageing.capabilities.other.IHunterSpecialAttributes;
import de.teamlapen.vampirism.entity.player.hunter.HunterPlayerSpecialAttribute;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;

@Mixin(HunterPlayerSpecialAttribute.class)
public class HunterPlayerSpecialAttributesMixin implements IHunterSpecialAttributes {


    @Unique
    private boolean batMode;

    @Unique
    private int ticksInSun;
    @Override
    public boolean ageing$getBatMode() {
        return batMode;
    }

    @Override
    public void ageing$setBatMode(boolean batMode) {
        this.batMode = batMode;
    }

    @Override
    public int ageing$getTicksInSun() {
        return ticksInSun;
    }

    @Override
    public void setTicksInSun(int ticks) {
        this.ticksInSun = ticks;

    }

}