package com.thedrofdoctoring.vampiricageing.capabilities.ageing.types;

import com.thedrofdoctoring.vampiricageing.VampiricAgeing;
import com.thedrofdoctoring.vampiricageing.capabilities.AgeingManager;
import com.thedrofdoctoring.vampiricageing.capabilities.CapabilityHelper;
import com.thedrofdoctoring.vampiricageing.capabilities.ageing.IAgeType;
import com.thedrofdoctoring.vampiricageing.config.HunterAgeingConfig;
import com.thedrofdoctoring.vampiricageing.skills.VampiricAgeingSkills;
import de.teamlapen.vampirism.api.VReference;
import de.teamlapen.vampirism.api.entity.factions.IPlayableFaction;
import de.teamlapen.vampirism.api.entity.player.hunter.IHunterPlayer;
import de.teamlapen.vampirism.api.entity.player.skills.ISkillHandler;
import de.teamlapen.vampirism.entity.player.hunter.HunterPlayer;
import net.minecraft.core.Holder;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.player.Player;
import org.jetbrains.annotations.NotNull;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

public class HunterAgeingType implements IAgeType {

    private static final String id = "HUNTER";
    @Override
    public IPlayableFaction<?> faction() {
        return VReference.HUNTER_FACTION;
    }

    @Override
    public Map<Holder<Attribute>, AttributeModifier> getAgeAttributes(int age, Player player, boolean cleanup) {
        HashMap<Holder<Attribute>, AttributeModifier> attributeMap = new HashMap<>();
        attributeMap.put(Attributes.MAX_HEALTH, new AttributeModifier(VampiricAgeing.rl("hunter_ageing_max_health"), HunterAgeingConfig.maxHealthIncrease.get().get(age), AttributeModifier.Operation.ADD_VALUE));
        attributeMap.put(Attributes.MOVEMENT_SPEED, new AttributeModifier(VampiricAgeing.rl("hunter_ageing_speed"), HunterAgeingConfig.movementSpeedBonus.get().get(age), AttributeModifier.Operation.ADD_VALUE));
        int cumulativeTaintedBloodAge = CapabilityHelper.getCumulativeTaintedAge(player);
        HunterState state = (HunterState) AgeingManager.getAge(player).getTypeState();
        if(state != null && state.taintedAgeBonus > 0) {
            attributeMap.put(Attributes.ATTACK_DAMAGE, new AttributeModifier(VampiricAgeing.rl("hunter_ageing_attack_damage"), HunterAgeingConfig.taintedDamageBonuses.get().get(cumulativeTaintedBloodAge), AttributeModifier.Operation.ADD_VALUE));
            attributeMap.put(Attributes.MAX_HEALTH, new AttributeModifier(VampiricAgeing.rl("hunter_tainted_ageing_max_health"), HunterAgeingConfig.taintedBloodMaxHealthIncreases.get().get(cumulativeTaintedBloodAge), AttributeModifier.Operation.ADD_VALUE));
            attributeMap.put(Attributes.MOVEMENT_SPEED, new AttributeModifier(VampiricAgeing.rl("hunter_tainted_ageing_speed"), HunterAgeingConfig.taintedBloodMovementSpeedIncreases.get().get(cumulativeTaintedBloodAge), AttributeModifier.Operation.ADD_VALUE));
        }
        if(cleanup) {
            attributeMap.put(Attributes.ATTACK_DAMAGE, new AttributeModifier(VampiricAgeing.rl("hunter_ageing_attack_damage"), HunterAgeingConfig.taintedDamageBonuses.get().get(1), AttributeModifier.Operation.ADD_VALUE));
            attributeMap.put(Attributes.MAX_HEALTH, new AttributeModifier(VampiricAgeing.rl("hunter_tainted_ageing_max_health"), HunterAgeingConfig.taintedBloodMaxHealthIncreases.get().get(1), AttributeModifier.Operation.ADD_VALUE));
            attributeMap.put(Attributes.MOVEMENT_SPEED, new AttributeModifier(VampiricAgeing.rl("hunter_tainted_ageing_speed"), HunterAgeingConfig.taintedBloodMovementSpeedIncreases.get().get(1), AttributeModifier.Operation.ADD_VALUE));
        }

        return attributeMap;
    }

    @Override
    public void handleSkills(int age, ServerPlayer player) {
        ISkillHandler<IHunterPlayer> skillHandler = HunterPlayer.get(player).getSkillHandler();

        int taintedAge = CapabilityHelper.getCumulativeTaintedAge(player);
        if(age >= HunterAgeingConfig.taintedBloodBottleAge.get()) {
            skillHandler.enableSkill(VampiricAgeingSkills.TAINTED_BLOOD_SKILL.get());
        } else {
            skillHandler.disableSkill(VampiricAgeingSkills.TAINTED_BLOOD_SKILL.get());
        }

        if(taintedAge >= HunterAgeingConfig.hunterTeleportActionAge.get()) {
            skillHandler.enableSkill(VampiricAgeingSkills.HUNTER_TELEPORT_SKILL.get());
        } else {
            skillHandler.disableSkill(VampiricAgeingSkills.HUNTER_TELEPORT_SKILL.get());
        }

        if(taintedAge >= HunterAgeingConfig.limitedBatModeAge.get()) {
            skillHandler.enableSkill(VampiricAgeingSkills.LIMITED_BAT_MODE_SKILL.get());
        } else {
            skillHandler.disableSkill(VampiricAgeingSkills.LIMITED_BAT_MODE_SKILL.get());
        }
    }

    @Override
    public int minFactionRank() {
        return HunterAgeingConfig.levelToBeginAgeMechanic.get();
    }

    @Override
    public boolean isEnabled() {
        return HunterAgeingConfig.hunterAgeing.get();
    }

    @Override
    public String getId() {
        return id;
    }

    @Override
    public String getAgeTitle(int ageRank) {
        if(ageRank > 0) {
            return HunterAgeingConfig.hunterAgeRankTitles.get().get(ageRank - 1);
        }
        return "";
    }

    @Override
    public Optional<HunterState> getStateType() {
        return Optional.of(new HunterState());
    }
    public static class HunterState extends TypeState {

        private int taintedAgeBonus = 0;
        private int taintedTicks = 0;
        private int ticksInSun;
        private boolean transformed = false;

        public int getTemporaryTaintedAgeBonus() {
            return this.taintedAgeBonus;
        }

        public void setTemporaryTaintedAgeBonus(int bonus) {
            taintedAgeBonus = bonus;
        }
        public int getTemporaryTainedTicks() {
            return this.taintedTicks;
        }
        public void setTicksInSun(int ticks) {
            ticksInSun = ticks;
        }
        public int getTicksInSun() {
            return this.ticksInSun;
        }

        public void setTemporaryTaintedTicks(int ticks) {
            this.taintedTicks = ticks;
        }
        public boolean isTransformed() {
            return this.transformed;
        }

        public void setTransformed(boolean transformed) {
            this.transformed = transformed;
        }

        @Override
        public void clear() {
            this.taintedAgeBonus = 0;
            this.taintedTicks = 0;
            this.ticksInSun = 0;
        }

        @Override
        public @NotNull CompoundTag serializeNBT(HolderLookup.@NotNull Provider provider, CompoundTag nbt) {
            nbt.putInt("ageing_tainted_age", taintedAgeBonus);
            nbt.putInt("ageing_tainted_ticks", taintedTicks);
            nbt.putInt("ageing_sun_ticks", ticksInSun);
            nbt.putBoolean("ageing_transformed", transformed);

            return nbt;
        }

        @Override
        public void deserializeNBT(HolderLookup.@NotNull Provider provider, @NotNull CompoundTag nbt) {
            if(nbt.getString("ageing_type").isEmpty()) {
                this.ticksInSun = 0;
                this.transformed = false;
                this.taintedTicks = 0;
                this.taintedAgeBonus = 0;
            } else {
                this.taintedAgeBonus = nbt.getInt("ageing_tainted_age");
                this.taintedTicks = nbt.getInt("ageing_tainted_ticks");
                this.ticksInSun = nbt.getInt("ageing_sun_ticks");
                this.transformed = nbt.getBoolean("ageing_transformed");
            }
        }

        @Override
        public void deserializeUpdateNBT(HolderLookup.@NotNull Provider provider, @NotNull CompoundTag nbt) {
            if(nbt.getString("ageing_type").isEmpty()) {
                this.ticksInSun = 0;
                this.transformed = false;
                this.taintedTicks = 0;
                this.taintedAgeBonus = 0;
            } else {
                this.taintedAgeBonus = nbt.getInt("ageing_tainted_age");
                this.taintedTicks = nbt.getInt("ageing_tainted_ticks");
                this.ticksInSun = nbt.getInt("ageing_sun_ticks");
                this.transformed = nbt.getBoolean("ageing_transformed");
            }
        }

        @Override
        public @NotNull CompoundTag serializeUpdateNBT(HolderLookup.@NotNull Provider provider, CompoundTag nbt) {
            nbt.putInt("ageing_tainted_age", taintedAgeBonus);
            nbt.putInt("ageing_tainted_ticks", taintedTicks);
            nbt.putInt("ageing_sun_ticks", ticksInSun);
            nbt.putBoolean("ageing_transformed", transformed);

            return nbt;
        }
    }
}
