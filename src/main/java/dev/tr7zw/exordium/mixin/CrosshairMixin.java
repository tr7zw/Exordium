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
//#if MC < 12006
//$$import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
//$$import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
//$$import dev.tr7zw.exordium.components.vanilla.DebugOverlayComponent;
//#endif

//#if MC >= 12100
import net.minecraft.client.DeltaTracker;
//#endif

@Mixin(Gui.class)
public class CrosshairMixin {

    @Shadow
    private DebugScreenOverlay debugOverlay;

    //#if MC >= 12005
    @Inject(method = "renderCrosshair", at = @At("HEAD"), cancellable = true)
    //#if MC >= 12100
    private void renderCrosshairStart(GuiGraphics guiGraphics, DeltaTracker delta, CallbackInfo ci) {
        //#else
        //$$ private void renderCrosshairStart(GuiGraphics guiGraphics, float f, CallbackInfo ci) {
        //#endif
        BufferInstance<DebugScreenOverlay> buffer = ExordiumModBase.instance.getBufferManager()
                .getBufferInstance(CrosshairComponent.getId(), DebugScreenOverlay.class);
        if (buffer.renderBuffer(debugOverlay, guiGraphics)) {
            ci.cancel();
        }

    }

    @Inject(method = "renderCrosshair", at = @At("TAIL"))
    //#if MC >= 12100
    private void renderCrosshairEnd(GuiGraphics guiGraphics, DeltaTracker delta, CallbackInfo ci) {
        //#else
        //$$ private void renderCrosshairEnd(GuiGraphics guiGraphics, float f, CallbackInfo ci) {
        //#endif
        BufferInstance<DebugScreenOverlay> buffer = ExordiumModBase.instance.getBufferManager()
                .getBufferInstance(CrosshairComponent.getId(), DebugScreenOverlay.class);
        buffer.postRender(debugOverlay, guiGraphics);
    }
    //#else
    //$$@WrapOperation(method = "render", at = {
    //$$        @At(value = "INVOKE", target = "Lnet/minecraft/client/gui/Gui;renderCrosshair(Lnet/minecraft/client/gui/GuiGraphics;)V"), })
    //$$private void renderCrosshairWrapper(Gui gui, GuiGraphics guiGraphics, final Operation<Void> operation) {
    //$$    BufferInstance<Void> buffer = ExordiumModBase.instance.getBufferManager()
    //$$            .getBufferInstance(CrosshairComponent.getId(), Void.class);
    //$$    if (!buffer.renderBuffer(null, guiGraphics)) {
    //$$        operation.call(gui, guiGraphics);
    //$$    }
    //$$    buffer.postRender(null, guiGraphics);
    //$$}
    //#endif

}
