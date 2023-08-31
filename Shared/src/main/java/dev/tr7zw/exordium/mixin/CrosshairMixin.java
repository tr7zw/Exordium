package dev.tr7zw.exordium.mixin;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;

import dev.tr7zw.exordium.ExordiumModBase;
import dev.tr7zw.exordium.util.BufferedComponent;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.gui.GuiGraphics;

@Mixin(Gui.class)
public class CrosshairMixin {

    @Shadow
    private Minecraft minecraft;

    private boolean wasRenderingF3 = false;
    private float lastPitch = 0;
    private float lastYaw = 0;

    private BufferedComponent crosshairBufferedComponent = new BufferedComponent(true,
            ExordiumModBase.instance.config.debugScreenSettings) {

        @Override
        public boolean needsRender() {
            if (wasRenderingF3 != minecraft.options.renderDebug) {
                return true;
            }
            if (wasRenderingF3) {
                return lastPitch != minecraft.getCameraEntity().getXRot()
                        || lastYaw != minecraft.getCameraEntity().getYRot();
            }
            return false;
        }

        @Override
        public void captureState() {
            wasRenderingF3 = minecraft.options.renderDebug;
            lastPitch = minecraft.getCameraEntity().getXRot();
            lastYaw = minecraft.getCameraEntity().getYRot();
        }
    };

    @WrapOperation(method = "render", at = {
            @At(value = "INVOKE", target = "Lnet/minecraft/client/gui/Gui;renderCrosshair(Lnet/minecraft/client/gui/GuiGraphics;)V"),
    })
    private void renderCrosshairWrapper(Gui gui, GuiGraphics guiGraphics, final Operation<Void> operation) {
        if (!crosshairBufferedComponent.render()) {
            operation.call(gui, guiGraphics);
        }
        crosshairBufferedComponent.renderEnd();
    }

}
