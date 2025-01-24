package dev.tr7zw.exordium.mixin;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;

import dev.tr7zw.exordium.ExordiumModBase;
import dev.tr7zw.exordium.components.BufferInstance;
import dev.tr7zw.exordium.components.vanilla.DebugOverlayComponent;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.DebugScreenOverlay;

@Mixin(Gui.class)
public class GuiDebugOverlayMixin {

    //#if MC >= 12005
    @WrapOperation(method = "method_55807", at = {
            @At(value = "INVOKE", target = "Lnet/minecraft/client/gui/components/DebugScreenOverlay;render(Lnet/minecraft/client/gui/GuiGraphics;)V"), })
    //#else
    //$$@WrapOperation(method = "render", at = {
    //$$        @At(value = "INVOKE", target = "Lnet/minecraft/client/gui/components/DebugScreenOverlay;render(Lnet/minecraft/client/gui/GuiGraphics;)V"), })
    //#endif
    private void renderDebugOverlayWrapper(DebugScreenOverlay overlay, GuiGraphics guiGraphics,
            final Operation<Void> operation) {
        BufferInstance<Void> buffer = ExordiumModBase.instance.getBufferManager()
                .getBufferInstance(DebugOverlayComponent.getId(), Void.class);
        if (!buffer.renderBuffer(null, guiGraphics)) {
            operation.call(overlay, guiGraphics);
        }
        buffer.postRender(null, guiGraphics);
    }

}
