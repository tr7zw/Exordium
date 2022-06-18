package dev.tr7zw.fastergui.mixin;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import com.mojang.blaze3d.vertex.PoseStack;

import dev.tr7zw.fastergui.FasterGuiModBase;
import dev.tr7zw.fastergui.util.BufferRenderer;
import net.minecraft.client.gui.Gui;

@Mixin(value= Gui.class, priority = 1500) // higher priority, so it also captures rendering happening at RETURN
public class GuiMixin {
    
    private BufferRenderer bufferRenderer = new BufferRenderer();
    
    
    @Inject(method = "render", at = @At(value="INVOKE", target = "Lnet/minecraft/client/Minecraft;getDeltaFrameTime()F"), cancellable = true)
    public void render(PoseStack arg, float g, CallbackInfo ci) {
        if(!FasterGuiModBase.instance.config.enabledGui) {
            return;
        }
        bufferRenderer.render(ci);
    }
    
    @Inject(method = "render", at = @At("RETURN"))
    public void renderEnd(PoseStack arg, float g, CallbackInfo ci) {
        if(!FasterGuiModBase.instance.config.enabledGui) {
            return;
        }
        bufferRenderer.renderEnd(1000/FasterGuiModBase.instance.config.targetFPSIngameGui);
    }



}
