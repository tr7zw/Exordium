package dev.tr7zw.fastergui.mixin;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;

import dev.tr7zw.fastergui.FasterGuiModBase;
import net.minecraft.client.gui.components.DebugScreenOverlay;

@Mixin(DebugScreenOverlay.class)
public class DebugScreenOverlayMixin {

    @Inject(method = "render", at = @At("HEAD"))
    public void render(PoseStack poseStack, CallbackInfo ci) {
        FasterGuiModBase.correctBlendMode();
        FasterGuiModBase.setForceBlend(true);
    }
    
    @Inject(method = "render", at = @At("TAIL"))
    public void renderEnd(PoseStack poseStack, CallbackInfo ci) {
        FasterGuiModBase.setForceBlend(false);
        RenderSystem.defaultBlendFunc();
    }
    
}
