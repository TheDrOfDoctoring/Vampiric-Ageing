package com.thedrofdoctoring.vampiricageing.mixin.client;

import com.llamalad7.mixinextras.injector.ModifyReturnValue;
import com.thedrofdoctoring.vampiricageing.capabilities.cache.AgeingPlayerCache;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.entity.LivingEntityRenderer;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(LivingEntityRenderer.class)
public class LivingEntityRendererMixin {

    @ModifyReturnValue(method = "isBodyVisible", at = @At("RETURN"))
    private boolean shouldRender(boolean shouldRender) {
        if(shouldRender) return true;
        return Minecraft.getInstance().player != null && AgeingPlayerCache.get(Minecraft.getInstance().player).hasBypassInvisibility;
    }
}
