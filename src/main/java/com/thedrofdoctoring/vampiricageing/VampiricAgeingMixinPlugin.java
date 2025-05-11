package com.thedrofdoctoring.vampiricageing;

import com.google.common.collect.ImmutableMap;
import net.neoforged.fml.loading.LoadingModList;
import org.objectweb.asm.tree.ClassNode;
import org.spongepowered.asm.mixin.extensibility.IMixinConfigPlugin;
import org.spongepowered.asm.mixin.extensibility.IMixinInfo;

import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.Supplier;

public final class VampiricAgeingMixinPlugin implements IMixinConfigPlugin {

    private static final Supplier<Boolean> WEREWOLVES_LOADED = () -> LoadingModList.get().getModFileById(VampiricAgeing.WEREWOLVES_MODID) != null;



    @Override
    public void onLoad(String mixinPackage) {

    }

    @Override
    public String getRefMapperConfig() {
        return null;
    }


    private static final Map<String, Supplier<Boolean>> CONDITIONS = ImmutableMap.of(
            "com.thedrofdoctoring.vampiricageing.mixin.HowlActionMixin", WEREWOLVES_LOADED,
            "com.thedrofdoctoring.vampiricageing.mixin.SilverOilMixin", WEREWOLVES_LOADED,
            "com.thedrofdoctoring.vampiricageing.mixin.WerewolfFormActionMixin", WEREWOLVES_LOADED
            
    );

    @Override
    public boolean shouldApplyMixin(String targetClassName, String mixinClassName) {
        return CONDITIONS.getOrDefault(mixinClassName, () -> true).get();
    }
    @Override
    public void acceptTargets(Set<String> myTargets, Set<String> otherTargets) {

    }

    @Override
    public List<String> getMixins() {
        return null;
    }

    @Override
    public void preApply(String targetClassName, ClassNode targetClass, String mixinClassName, IMixinInfo mixinInfo) {

    }

    @Override
    public void postApply(String targetClassName, ClassNode targetClass, String mixinClassName, IMixinInfo mixinInfo) {

    }
}
