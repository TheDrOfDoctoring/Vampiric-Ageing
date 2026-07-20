package com.thedrofdoctoring.vampiricageing.mixin;

import com.thedrofdoctoring.vampiricageing.capabilities.AgeingManager;
import com.thedrofdoctoring.vampiricageing.capabilities.CapabilityHelper;
import com.thedrofdoctoring.vampiricageing.config.CommonConfig;
import com.thedrofdoctoring.vampiricageing.config.HunterAgeingConfig;
import de.teamlapen.vampirism.core.ModTags;
import de.teamlapen.vampirism.util.Helper;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.npc.AbstractVillager;
import net.minecraft.world.entity.npc.Villager;
import net.minecraft.world.entity.npc.VillagerData;
import net.minecraft.world.entity.npc.VillagerProfession;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.trading.MerchantOffer;
import net.minecraft.world.level.Level;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(Villager.class)
public abstract class VillagerMixin extends AbstractVillager {
    @Shadow public abstract VillagerData getVillagerData();

    public VillagerMixin(EntityType<? extends AbstractVillager> p_35267_, Level p_35268_) {
        super(p_35267_, p_35268_);
    }

    @Inject(method = "updateSpecialPrices", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/entity/npc/Villager;getPlayerReputation(Lnet/minecraft/world/entity/player/Player;)I", shift = At.Shift.BEFORE))
    private void updateSpecialPrices(Player player, CallbackInfo ci) {
        if (CommonConfig.doesAgeAffectPrices.get()) {
            VillagerProfession profession = this.getVillagerData().getProfession();
            if(!BuiltInRegistries.VILLAGER_PROFESSION.wrapAsHolder(profession).is(ModTags.Professions.HAS_FACTION)) {
                int age = AgeingManager.getAge(player).getAge();
                int cumulativeAge = CapabilityHelper.getCumulativeTaintedAge(player);
                boolean isVampire = Helper.isVampire(player);
                boolean isHunter  = Helper.isHunter(player);
                if(isVampire || cumulativeAge >= HunterAgeingConfig.taintedBloodWorseTradeDealsAge.get()) {
                    for(MerchantOffer merchantoffer1 : this.getOffers()) {
                        double ageMult = !Helper.isHunter(player) ? CommonConfig.ageAffectTradePrices.get().get(age) : HunterAgeingConfig.taintedBloodTradeDealPricesMultiplier.get().get(cumulativeAge).floatValue();
                        double d0 = 1 - ageMult;

                        int j = d0 != 0 ? (int)Math.floor((merchantoffer1.getBaseCostA().getCount()) * (ageMult - 1)) : 0;
                        merchantoffer1.addToSpecialPriceDiff(j);
                    }
                } else if(isHunter && cumulativeAge == 0 && age > 0) {
                    double mult = HunterAgeingConfig.regularTradeDealPricesMultiplier.get().get(age) - 1;
                    for (MerchantOffer merchantoffer1 : this.getOffers()) {
                        int adjusted = (int) Math.floor((merchantoffer1.getBaseCostA().getCount()) * mult);
                        int j = mult != 0 ? adjusted : 0;
                        merchantoffer1.addToSpecialPriceDiff(j);
                    }
                }
            }
        }
    }
}
