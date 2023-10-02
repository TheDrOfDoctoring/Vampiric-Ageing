package com.doctor.vampiricageing.actions;

import com.doctor.vampiricageing.capabilities.VampiricAgeingCapabilityManager;
import com.doctor.vampiricageing.config.CommonConfig;
import com.doctor.vampiricageing.config.HunterAgeingConfig;
import de.teamlapen.lib.lib.util.UtilLib;
import de.teamlapen.vampirism.api.entity.player.hunter.DefaultHunterAction;
import de.teamlapen.vampirism.api.entity.player.hunter.IHunterPlayer;
import de.teamlapen.vampirism.core.ModEntities;
import de.teamlapen.vampirism.entity.AreaParticleCloudEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.SoundEvents;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.BlockRayTraceResult;
import net.minecraft.util.math.EntityRayTraceResult;
import net.minecraft.util.math.RayTraceResult;


public class HunterTeleportAction extends DefaultHunterAction {

    public HunterTeleportAction() {
        super();
    }

    @Override
    public boolean activate(IHunterPlayer hunter, ActivationContext context) {
        //This is effectively just a copy of the vampire teleport action, so credit to the developers of vampirism (and credit to them for many other things :P)
        PlayerEntity player = hunter.getRepresentingPlayer();
        int dist = HunterAgeingConfig.hunterTeleportActionMaxDistance.get();
        RayTraceResult target = UtilLib.getPlayerLookingSpot(player, dist);
        double ox = player.getX();
        double oy = player.getY();
        double oz = player.getZ();
        if (target.getType() == RayTraceResult.Type.MISS) {
            player.playSound(SoundEvents.NOTE_BLOCK_BASS, 1, 1);
            return false;
        }

        BlockPos pos = null;
        if (target.getType() == RayTraceResult.Type.BLOCK) {
            if (player.getCommandSenderWorld().getBlockState(((BlockRayTraceResult) target).getBlockPos()).getMaterial().blocksMotion()) {
                pos = ((BlockRayTraceResult) target).getBlockPos().above();
            }
        } else {
            if (player.getCommandSenderWorld().getBlockState(((EntityRayTraceResult) target).getEntity().blockPosition()).getMaterial().blocksMotion()) {
                pos = ((EntityRayTraceResult) target).getEntity().blockPosition();
            }
        }

        if (pos != null) {
            player.setPos(pos.getX() + 0.5, pos.getY() + 0.1, pos.getZ() + 0.5);
            if (player.getCommandSenderWorld().containsAnyLiquid(player.getBoundingBox()) || !player.getCommandSenderWorld().isUnobstructed(player)) {
                pos = null;
            }
        }


        if (pos == null) {
            player.setPos(ox, oy, oz);
            player.playSound(SoundEvents.NOTE_BLOCK_BASEDRUM, 1, 1);
            return false;
        }
        if (player instanceof ServerPlayerEntity) {
            ServerPlayerEntity playerMp = (ServerPlayerEntity) player;
            playerMp.disconnect();
            playerMp.teleportTo(pos.getX() + 0.5, pos.getY() + 0.1, pos.getZ() + 0.5);
        }
        AreaParticleCloudEntity particleCloud = new AreaParticleCloudEntity(ModEntities.PARTICLE_CLOUD.get(), player.getCommandSenderWorld());
        particleCloud.setPos(ox, oy, oz);
        particleCloud.setRadius(0.7F);
        particleCloud.setHeight(player.getBbHeight());
        particleCloud.setDuration(5);
        particleCloud.setSpawnRate(15);
        player.getCommandSenderWorld().addFreshEntity(particleCloud);
        player.getCommandSenderWorld().playLocalSound(ox, oy, oz, SoundEvents.ENDERMAN_TELEPORT, SoundCategory.PLAYERS, 1.0F, 1.0F, false);
        player.getCommandSenderWorld().playLocalSound(player.getX(), player.getY(), player.getZ(), SoundEvents.ENDERMAN_TELEPORT, SoundCategory.PLAYERS, 1, 1, false);
        return true;
    }

    @Override
    public boolean canBeUsedBy(@NotNull IHunterPlayer hunter) {
        return CapabilityHelper.getCumulativeTaintedAge(hunter.getRepresentingPlayer()) >= HunterAgeingConfig.hunterTeleportActionAge.get();
    }

    @Override
    public boolean isEnabled() {
        return HunterAgeingConfig.hunterTeleportAction.get();
    }

    @Override
    public int getCooldown() {
        return HunterAgeingConfig.hunterTeleportActionCooldown.get() * 20;
    }
}
