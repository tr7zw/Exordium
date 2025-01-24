package dev.tr7zw.exordium.mixin;

import org.lwjgl.opengl.GL14;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import com.mojang.blaze3d.platform.GlStateManager;

import dev.tr7zw.exordium.ExordiumModBase;

/**
 * Really "concerning" workaround to minecraft(Lore overlay) and mods(REI) using
 * really odd and broken blend functions
 *
 */
@Mixin(value = GlStateManager.class, remap = false)
public class GlStateManagerMixin {

    @Inject(method = "_blendFunc", at = @At("HEAD"), cancellable = true)
    private static void _blendFunc(int i, int j, CallbackInfo ci) {
        if (ExordiumModBase.isForceBlend()) {
            GlStateManager.glBlendFuncSeparate(i, j, i, j);
            ci.cancel();
        }
    }

    @Inject(method = "_blendFuncSeparate", at = @At("HEAD"), cancellable = true)
    private static void _blendFuncSeparate(int i, int j, int k, int l, CallbackInfo ci) {
        if (ExordiumModBase.isForceBlend()) {
            GlStateManager.glBlendFuncSeparate(i, j, k, l);
            ci.cancel();
        }
    }

    @Inject(method = "glBlendFuncSeparate", at = @At("HEAD"), cancellable = true)
    private static void glBlendFuncSeparate(int i, int j, int k, int l, CallbackInfo ci) {
        if (ExordiumModBase.isForceBlend()) {
            GL14.glBlendFuncSeparate(GlStateManager.SourceFactor.SRC_ALPHA.value,
                    GlStateManager.DestFactor.ONE_MINUS_SRC_ALPHA.value, GlStateManager.SourceFactor.ONE.value,
                    GlStateManager.DestFactor.ONE_MINUS_SRC_ALPHA.value);
            ci.cancel();
        }
    }

}
