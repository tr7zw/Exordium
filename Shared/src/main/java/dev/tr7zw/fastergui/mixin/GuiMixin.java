package dev.tr7zw.fastergui.mixin;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.At.Shift;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import com.mojang.blaze3d.systems.RenderSystem;
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
        boolean cancel = bufferRenderer.render();
        if(cancel)
            ci.cancel();
    }
    
    @Inject(method = "render", at = @At("RETURN"))
    public void renderEnd(PoseStack arg, float g, CallbackInfo ci) {
        if(!FasterGuiModBase.instance.config.enabledGui) {
            return;
        }
        bufferRenderer.renderEnd(1000/FasterGuiModBase.instance.config.targetFPSIngameGui);
    }

    // Fix for AppleSkin
    
    @Inject(method = "renderPlayerHealth", at = @At("HEAD"))
    private void renderPlayerHealth(PoseStack poseStack, CallbackInfo ci) {
        FasterGuiModBase.correctBlendMode();
        FasterGuiModBase.setForceBlend(true);
    }
    
    @Inject(method = "renderPlayerHealth", at = @At("RETURN"))
    private void renderPlayerHealthReturn(PoseStack poseStack, CallbackInfo ci) {
        FasterGuiModBase.setForceBlend(false);
    }
    
    // Fix for chat breaking the armor bar outline
    
    @Inject(method = "render", at = @At(value="INVOKE", target = "Lnet/minecraft/client/gui/components/ChatComponent;render(Lcom/mojang/blaze3d/vertex/PoseStack;I)V", shift = Shift.BEFORE))
    public void renderChat(PoseStack arg, float g, CallbackInfo ci) {
        FasterGuiModBase.correctBlendMode();
        FasterGuiModBase.setForceBlend(true);
    }
    
    @Inject(method = "render", at = @At(value="INVOKE", target = "Lnet/minecraft/client/gui/components/ChatComponent;render(Lcom/mojang/blaze3d/vertex/PoseStack;I)V", shift = Shift.AFTER))
    public void renderChatEnd(PoseStack arg, float g, CallbackInfo ci) {
        FasterGuiModBase.setForceBlend(false);
        RenderSystem.defaultBlendFunc();
    }
    
    // Fix for tablist
    
    @Inject(method = "render", at = @At(value="INVOKE", target = "Lnet/minecraft/client/gui/components/PlayerTabOverlay;render(Lcom/mojang/blaze3d/vertex/PoseStack;ILnet/minecraft/world/scores/Scoreboard;Lnet/minecraft/world/scores/Objective;)V", shift = Shift.BEFORE))
    public void renderTab(PoseStack arg, float g, CallbackInfo ci) {
        FasterGuiModBase.correctBlendMode();
        FasterGuiModBase.setForceBlend(true);
    }
    
    @Inject(method = "render", at = @At(value="INVOKE", target = "Lnet/minecraft/client/gui/components/PlayerTabOverlay;render(Lcom/mojang/blaze3d/vertex/PoseStack;ILnet/minecraft/world/scores/Scoreboard;Lnet/minecraft/world/scores/Objective;)V", shift = Shift.AFTER))
    public void renderTabEnd(PoseStack arg, float g, CallbackInfo ci) {
        FasterGuiModBase.setForceBlend(false);
        RenderSystem.defaultBlendFunc();
    }
    
    // Fix Scoreboard overlapping with overlays like spyglass
    
    @Inject(method = "render", at = @At(value="INVOKE", target = "Lnet/minecraft/client/gui/Gui;displayScoreboardSidebar(Lcom/mojang/blaze3d/vertex/PoseStack;Lnet/minecraft/world/scores/Objective;)V", shift = Shift.BEFORE))
    private void displayScoreboardSidebarBefore(PoseStack arg, float g, CallbackInfo ci) {
        FasterGuiModBase.correctBlendMode();
        FasterGuiModBase.setForceBlend(true);
    }
    
    @Inject(method = "render", at = @At(value="INVOKE", target = "Lnet/minecraft/client/gui/Gui;displayScoreboardSidebar(Lcom/mojang/blaze3d/vertex/PoseStack;Lnet/minecraft/world/scores/Objective;)V", shift = Shift.AFTER))
    private void displayScoreboardSidebarAfter(PoseStack arg, float g, CallbackInfo ci) {
        FasterGuiModBase.setForceBlend(false);
        RenderSystem.defaultBlendFunc();
    }

}
