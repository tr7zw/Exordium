package dev.tr7zw.exordium.mixin;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;

import dev.tr7zw.exordium.ExordiumModBase;
import dev.tr7zw.exordium.access.VanillaBufferAccess.DebugOverlayAccess;
import dev.tr7zw.exordium.util.BufferedComponent;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.DebugScreenOverlay;

@Mixin(Gui.class)
public class GuiDebugOverlayMixin implements DebugOverlayAccess {

    private BufferedComponent debugBufferedComponent = new BufferedComponent(true,
            () -> ExordiumModBase.instance.config.debugScreenSettings) {

        @Override
        public boolean needsRender() {
            return true;
        }

        @Override
        public void captureState() {
        }
    };

    @WrapOperation(method = "method_55807", at = {
            @At(value = "INVOKE", target = "Lnet/minecraft/client/gui/components/DebugScreenOverlay;render(Lnet/minecraft/client/gui/GuiGraphics;)V"), })
    private void renderExperienceBarWrapper(DebugScreenOverlay overlay, GuiGraphics guiGraphics,
            final Operation<Void> operation) {
        if (!debugBufferedComponent.render()) {
            operation.call(overlay, guiGraphics);
        }
        debugBufferedComponent.renderEnd();
    }

    @Override
    public BufferedComponent getDebugOverlayBuffer() {
        return debugBufferedComponent;
    }

}
