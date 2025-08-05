package dev.tr7zw.exordium.mixin;

import java.util.List;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import com.mojang.blaze3d.buffers.GpuBufferSlice;
import com.mojang.blaze3d.systems.RenderPass.Draw;

import dev.tr7zw.exordium.ExordiumModBase;
import dev.tr7zw.exordium.components.BufferInstance;
import net.minecraft.client.gui.render.GuiRenderer;

@Mixin(GuiRenderer.class)
public class GuiRendererMixin {

    @Shadow
    private List<Draw> draws;

    @Inject(method = "prepare", at = @At("HEAD"), cancellable = true)
    private void prepare(CallbackInfo ci) {

    }

    @WrapOperation(method = "render", at = {
            @At(value = "INVOKE", target = "Lnet/minecraft/client/gui/render/GuiRenderer;draw(Lcom/mojang/blaze3d/buffers/GpuBufferSlice;)V"), })
    private void drawWrapper(GuiRenderer guiRenderer, GpuBufferSlice gpuBufferSlice, final Operation<Void> operation) {
        if (!this.draws.isEmpty()) {
            BufferInstance buffer = ExordiumModBase.instance.getMainBuffer();
            if (!buffer.renderBuffer()) {
                operation.call(guiRenderer, gpuBufferSlice);
            }
            buffer.postRender();
        }
    }

}
