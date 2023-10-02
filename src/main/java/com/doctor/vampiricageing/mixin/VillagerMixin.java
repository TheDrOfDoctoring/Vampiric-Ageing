package com.doctor.vampiricageing.mixin;

import com.doctor.vampiricageing.capabilities.VampiricAgeingCapabilityManager;
import com.doctor.vampiricageing.config.CommonConfig;
import de.teamlapen.vampirism.core.ModTags;
import de.teamlapen.vampirism.entity.FactionVillagerProfession;
import de.teamlapen.vampirism.util.Helper;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.merchant.villager.AbstractVillagerEntity;
import net.minecraft.entity.merchant.villager.VillagerData;
import net.minecraft.entity.merchant.villager.VillagerEntity;
import net.minecraft.entity.merchant.villager.VillagerProfession;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.MerchantOffer;
import net.minecraft.world.World;
import net.minecraftforge.registries.ForgeRegistries;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(VillagerEntity.class)
public abstract class VillagerMixin extends AbstractVillagerEntity {
    @Shadow public abstract VillagerData getVillagerData();

    public VillagerMixin(EntityType<? extends AbstractVillagerEntity> p_35267_, World p_35268_) {
        super(p_35267_, p_35268_);
    }

    @Inject(method = "updateSpecialPrices", at = @At(value = "INVOKE", target = "Lnet/minecraft/entity/player/PlayerEntity;hasEffect(Lnet/minecraft/potion/Effect;)Z", shift = At.Shift.BEFORE))
    private void updateSpecialPrices(PlayerEntity player, CallbackInfo ci) {
        if (!Helper.isHunter(player) && CommonConfig.doesAgeAffectPrices.get()) {
            if(!(getVillagerData().getProfession() instanceof FactionVillagerProfession)) {
                int age = VampiricAgeingCapabilityManager.getAge(player).map(ageCap -> ageCap.getAge()).orElse(0);
                int cumulativeAge = CapabilityHelper.getCumulativeTaintedAge(player);
                if(!Helper.isHunter(player) || cumulativeAge >= HunterAgeingConfig.taintedBloodWorseTradeDealsAge.get()) {
                    for(MerchantOffer merchantoffer1 : this.getOffers()) {
                        double ageMult = !Helper.isHunter(player) ? CommonConfig.ageAffectTradePrices.get().get(age) : HunterAgeingConfig.taintedBloodTradeDealPricesMultiplier.get().get(cumulativeAge);
                        double d0 = 1 - ageMult;

                    int j = d0 != 0 ? (int)Math.floor((merchantoffer1.getBaseCostA().getCount()) * (ageMult - 1)) : 0;
                    merchantoffer1.addToSpecialPriceDiff(j);
                }
            }
        }
    }
}
