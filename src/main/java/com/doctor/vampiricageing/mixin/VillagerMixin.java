package com.doctor.vampiricageing.mixin;

import com.doctor.vampiricageing.capabilities.VampiricAgeingCapabilityManager;
import com.doctor.vampiricageing.config.CommonConfig;
import de.teamlapen.vampirism.util.Helper;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.npc.AbstractVillager;
import net.minecraft.world.entity.npc.Villager;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.trading.MerchantOffer;
import net.minecraft.world.level.Level;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(Villager.class)
public abstract class VillagerMixin extends AbstractVillager {
    public VillagerMixin(EntityType<? extends AbstractVillager> p_35267_, Level p_35268_) {
        super(p_35267_, p_35268_);
    }

    @Inject(method = "updateSpecialPrices", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/entity/player/Player;hasEffect(Lnet/minecraft/world/effect/MobEffect;)Z", shift = At.Shift.BEFORE))
    private void updateSpecialPrices(Player player, CallbackInfo ci) {
        if (Helper.isVampire(player) && CommonConfig.doesAgeAffectPrices.get()) {
            int age = VampiricAgeingCapabilityManager.getAge(player).map(ageCap -> ageCap.getAge()).orElse(0);
            for(MerchantOffer merchantoffer1 : this.getOffers()) {
                float ageMult = CommonConfig.ageAffectTradePrices.get().get(age);
                double d0 = 1 - ageMult;

                int j = d0 != 0 ? (int)Math.floor((merchantoffer1.getBaseCostA().getCount()) * (ageMult - 1)) : 0;
                merchantoffer1.addToSpecialPriceDiff(j);
            }
        }
    }
}