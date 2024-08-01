package dev.tr7zw.exordium.mixin.support;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;

import dev.tr7zw.exordium.ExordiumModBase;
import dev.tr7zw.exordium.components.BufferInstance;
import dev.tr7zw.exordium.components.support.XaerosMinimapComponent;
import net.minecraft.client.gui.GuiGraphics;
import xaero.common.events.ModClientEvents;
import xaero.hud.Hud;
import xaero.hud.render.HudRenderer;

@Mixin(ModClientEvents.class)
public class XaerosMinimapMixin {

    @WrapOperation(method = "handleRenderModOverlay", at = { @At(value = "INVOKE", target = "render") }, remap = false)
    private void renderMinimap(HudRenderer renderer, Hud hud, GuiGraphics guiGraphics, float tick,
            final Operation<Void> operation) {
        BufferInstance<Void> buffer = ExordiumModBase.instance.getBufferManager()
                .getBufferInstance(XaerosMinimapComponent.getId(), Void.class);
        if (!buffer.renderBuffer(0, null)) {
            operation.call(renderer, hud, guiGraphics, tick);
        }
        buffer.postRender(null);
    }

}
