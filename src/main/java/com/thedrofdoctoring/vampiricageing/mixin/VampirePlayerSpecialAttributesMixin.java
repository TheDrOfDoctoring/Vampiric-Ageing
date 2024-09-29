package com.thedrofdoctoring.vampiricageing.mixin;

import com.thedrofdoctoring.vampiricageing.capabilities.other.IVampSpecialAttributes;
import de.teamlapen.vampirism.entity.player.vampire.VampirePlayerSpecialAttributes;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;

@Mixin(VampirePlayerSpecialAttributes.class)
public class VampirePlayerSpecialAttributesMixin implements IVampSpecialAttributes {
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
