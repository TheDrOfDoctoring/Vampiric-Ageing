package com.doctor.vampiricageing;

import com.google.common.collect.ImmutableMap;
import net.minecraftforge.fml.ModList;
import net.minecraftforge.fml.loading.LoadingModList;
import org.objectweb.asm.tree.ClassNode;
import org.spongepowered.asm.mixin.extensibility.IMixinConfigPlugin;
import org.spongepowered.asm.mixin.extensibility.IMixinInfo;

import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.Supplier;

public final class VampiricAgeingMixinPlugin implements IMixinConfigPlugin {
    @Override
    public void onLoad(String mixinPackage) {

    }

    @Override
    public String getRefMapperConfig() {
        return null;
    }
    private static final Supplier<Boolean> TRUE = () -> true;
    private static final Map<String, Supplier<Boolean>> CONDITIONS = ImmutableMap.of(
            "com.doctor.vampiricageing.mixin.HowlActionMixin",() -> LoadingModList.get().getModFileById(VampiricAgeing.WEREWOLVES_MODID) != null,
            "com.doctor.vampiricageing.mixin.SilverOilMixin", () -> LoadingModList.get().getModFileById(VampiricAgeing.WEREWOLVES_MODID) != null

    );

    @Override
    public boolean shouldApplyMixin(String targetClassName, String mixinClassName) {
        return CONDITIONS.getOrDefault(mixinClassName, TRUE).get();
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
