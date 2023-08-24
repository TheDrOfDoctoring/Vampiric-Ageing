package com.doctor.vampiricageing.mixin;

import com.doctor.vampiricageing.capabilities.VampiricAgeingCapabilityManager;
import com.doctor.vampiricageing.config.CommonConfig;
import de.teamlapen.vampirism.util.Helper;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.tags.FluidTags;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.material.FluidState;
import net.minecraft.world.phys.shapes.*;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(BlockBehaviour.BlockStateBase.class)
public abstract class BlockBehaviourMixin {
    @Unique
    public final VoxelShape voxelShape = Shapes.block();
    @Shadow public abstract FluidState getFluidState();

    @Inject(method = "getCollisionShape(Lnet/minecraft/world/level/BlockGetter;Lnet/minecraft/core/BlockPos;Lnet/minecraft/world/phys/shapes/CollisionContext;)Lnet/minecraft/world/phys/shapes/VoxelShape;", at = @At(value = "RETURN"), cancellable = true)
    private void getCollisionShape(BlockGetter getter, BlockPos pos,  CollisionContext con, CallbackInfoReturnable<VoxelShape> cir) {
        if (getFluidState().is(FluidTags.WATER) && con instanceof EntityCollisionContext context && CommonConfig.ageWaterWalking.get()) {
            Entity entity = context.getEntity();
            if(entity instanceof Player player && Helper.isVampire(player) && vampiricageing$isAbove(entity, voxelShape, pos) && !entity.isInWater() && !entity.isShiftKeyDown()) {
                   int age = VampiricAgeingCapabilityManager.getAge(player).map(ageCap -> ageCap.getAge()).orElse(0);
                   if(age >= CommonConfig.ageWaterWalkingRank.get()) {
                       cir.setReturnValue(voxelShape);
                   }
            }
        }
    }
    @Unique
    private boolean vampiricageing$isAbove(Entity entity, VoxelShape shape, BlockPos pos) {
        return entity.getY() > pos.getY() + shape.max(Direction.Axis.Y) - (entity.isOnGround() ? 8.05/16.0 : 0.0015);
    }
}
