package dev.tr7zw.fastergui.mixin;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import com.mojang.blaze3d.platform.GlStateManager;

import dev.tr7zw.fastergui.FasterGuiModBase;

@Mixin(value = GlStateManager.class, remap = false)
public class GlStateManagerMixin {

    @Inject(method = "_blendFunc", at = @At("HEAD"), cancellable = true)
    private static void _blendFunc(int i, int j, CallbackInfo ci) {
        if(FasterGuiModBase.isForceBlend() && !FasterGuiModBase.isBlendBypass()) {
            ci.cancel();
        }
    }

    @Inject(method = "_blendFuncSeparate", at = @At("HEAD"), cancellable = true)
    private static void _blendFuncSeparate(int i, int j, int k, int l, CallbackInfo ci) {
        if(FasterGuiModBase.isForceBlend() && !FasterGuiModBase.isBlendBypass()) {
            ci.cancel();
        }
    }
    
}
