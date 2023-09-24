package com.doctor.vampiricageing.mixin;

import com.doctor.vampiricageing.capabilities.VampiricAgeingCapabilityManager;
import com.doctor.vampiricageing.config.CommonConfig;
import de.teamlapen.vampirism.util.Helper;
import net.minecraft.block.AbstractBlock;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.fluid.FluidState;
import net.minecraft.fluid.Fluids;
import net.minecraft.tags.FluidTags;
import net.minecraft.util.Direction;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.shapes.ISelectionContext;
import net.minecraft.util.math.shapes.VoxelShape;
import net.minecraft.util.math.shapes.VoxelShapes;
import net.minecraft.world.IBlockReader;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(AbstractBlock.AbstractBlockState.class)
public abstract class BlockBehaviourMixin {
    @Unique
    public final VoxelShape voxelShape = VoxelShapes.block();
    @Shadow public abstract FluidState getFluidState();

    @Inject(method = "getCollisionShape(Lnet/minecraft/world/IBlockReader;Lnet/minecraft/util/math/BlockPos;Lnet/minecraft/util/math/shapes/ISelectionContext;)Lnet/minecraft/util/math/shapes/VoxelShape;", at = @At(value = "RETURN"), cancellable = true)
    private void getCollisionShape(IBlockReader reader, BlockPos pos, ISelectionContext con, CallbackInfoReturnable<VoxelShape> cir) {
        if (getFluidState() == Fluids.WATER.defaultFluidState() && CommonConfig.ageWaterWalking.get()) {
            Entity entity = con.getEntity();
            if(entity instanceof PlayerEntity && Helper.isVampire(entity) && vampiricageing$isAbove(entity, voxelShape, pos) && !entity.isInWater() && !entity.isShiftKeyDown()) {
                   PlayerEntity player = (PlayerEntity) entity;
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
