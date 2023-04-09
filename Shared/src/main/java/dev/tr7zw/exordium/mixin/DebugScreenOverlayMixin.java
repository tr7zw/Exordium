package dev.tr7zw.exordium.mixin;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;

import dev.tr7zw.exordium.ExordiumModBase;
import dev.tr7zw.exordium.util.BufferedComponent;
import net.minecraft.client.gui.components.DebugScreenOverlay;

@Mixin(DebugScreenOverlay.class)
public class DebugScreenOverlayMixin {

    private BufferedComponent bufferedComponent = new BufferedComponent(ExordiumModBase.instance.config.debugScreenSettings) {
        
        @Override
        public boolean needsRender() {
            return true;
        }

        @Override
        public void captureState() {
        }
    };
    
    @Inject(method = "render", at = @At("HEAD"), cancellable = true)
    public void render(PoseStack poseStack, CallbackInfo ci) {
        if(bufferedComponent.render()) {
            ci.cancel();
            return;
        }
        ExordiumModBase.correctBlendMode();
        ExordiumModBase.setForceBlend(true);
    }
    
    @Inject(method = "render", at = @At("TAIL"))
    public void renderEnd(PoseStack poseStack, CallbackInfo ci) {
        ExordiumModBase.setForceBlend(false);
        RenderSystem.defaultBlendFunc();
        bufferedComponent.renderEnd();
    }
    
}
