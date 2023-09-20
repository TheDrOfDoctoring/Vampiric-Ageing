package com.doctor.vampiricageing.mixin;

import com.doctor.vampiricageing.config.CommonConfig;
import de.teamlapen.vampirism.util.Helper;
import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.PowderSnowBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.Vec3;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(PowderSnowBlock.class)
public class PowderSnowBlockMixin {

    @Inject(method = "entityInside", at = @At(value = "HEAD"), cancellable = true)
    private void entityInside(BlockState state, Level level, BlockPos pos, Entity entity, CallbackInfo ci ) {
        if(CommonConfig.vampirePowderedSnowImmunity.get() && Helper.isVampire(entity)) {
            entity.makeStuckInBlock(state, new Vec3(0.9F, 1.5D, 0.9F));
            ci.cancel();
        }
    }
}
