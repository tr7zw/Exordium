package dev.tr7zw.exordium.mixin;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import dev.tr7zw.exordium.ExordiumModBase;
import dev.tr7zw.exordium.components.BufferInstance;
import dev.tr7zw.exordium.components.vanilla.CrosshairComponent;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.DebugScreenOverlay;

@Mixin(Gui.class)
public class CrosshairMixin {

    @Shadow
    private DebugScreenOverlay debugOverlay;

    @Inject(method = "renderCrosshair", at = @At("HEAD"), cancellable = true)
    private void renderCrosshairStart(GuiGraphics guiGraphics, float f, CallbackInfo ci) {
        BufferInstance<DebugScreenOverlay> buffer = ExordiumModBase.instance.getBufferManager()
                .getBufferInstance(CrosshairComponent.getId(), DebugScreenOverlay.class);
        if (buffer.renderBuffer(0, debugOverlay)) {
            ci.cancel();
        }

    }

    @Inject(method = "renderCrosshair", at = @At("TAIL"))
    private void renderCrosshairEnd(GuiGraphics guiGraphics, float f, CallbackInfo ci) {
        BufferInstance<DebugScreenOverlay> buffer = ExordiumModBase.instance.getBufferManager()
                .getBufferInstance(CrosshairComponent.getId(), DebugScreenOverlay.class);
        buffer.postRender(debugOverlay);
    }

}
