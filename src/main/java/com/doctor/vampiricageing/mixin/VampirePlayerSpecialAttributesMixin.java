package com.doctor.vampiricageing.mixin;

import com.doctor.vampiricageing.capabilities.ISpecialAttributes;
import de.teamlapen.vampirism.entity.player.vampire.VampirePlayerSpecialAttributes;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;

@Mixin(VampirePlayerSpecialAttributes.class)
public class VampirePlayerSpecialAttributesMixin implements ISpecialAttributes {
    @Unique
    public boolean ageing$waterWalking;

    @Override
    public boolean ageing$getWaterWalking() {
        return ageing$waterWalking;
    }

    @Override
    public void ageing$setWaterWalking(boolean waterWalking) {
        this.ageing$waterWalking = waterWalking;
    }
}
