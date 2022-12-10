package dev.tr7zw.exordium.mixin;

import org.joml.Matrix4f;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.At.Shift;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import com.mojang.blaze3d.vertex.PoseStack;

import dev.tr7zw.exordium.ExordiumModBase;
import dev.tr7zw.exordium.util.NametagScreenBuffer;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.GameRenderer;

@Mixin(value = GameRenderer.class, priority = 500) //needs to be lower to cancel Architectury for REI
public abstract class GameRendererMixinLow {

    @Shadow
    @Final
    private Minecraft minecraft;
    
    @Inject(method = "render(FJZ)V", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/gui/screens/Screen;renderWithTooltip(Lcom/mojang/blaze3d/vertex/PoseStack;IIF)V", ordinal = 0), cancellable = true)
    public void renderScreenPre(float tickDelta, long startTime, boolean tick, CallbackInfo ci) {
        if(!ExordiumModBase.instance.config.enabledScreensLegacy || minecraft.level == null) {
            return;
        }

        boolean cancel = ExordiumModBase.instance.getScreenBufferRenderer().render();
        if(cancel)
            ci.cancel();
    }
    
    @Inject(method = "render(FJZ)V", at = @At(value = "INVOKE", target = "Lcom/mojang/blaze3d/platform/Lighting;setupFor3DItems()V", ordinal = 0, shift = Shift.AFTER))
    public void renderLevel(float tickDelta, long startTime, boolean tick, CallbackInfo info) {
        NametagScreenBuffer buffer = ExordiumModBase.instance.getNameTagScreenBuffer();
        buffer.renderOverlay();
        Minecraft.getInstance().getMainRenderTarget().bindWrite(true);
    }
    
    @Inject(method = "render(FJZ)V", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/Minecraft;getWindow()Lcom/mojang/blaze3d/platform/Window;", ordinal = 0, shift = Shift.AFTER))
    public void postWorldRender(float tickDelta, long startTime, boolean tick, CallbackInfo ci) {
        ExordiumModBase.instance.getDelayedRenderCallManager().execRenderCalls();
    }
    
    @Redirect(method = "renderLevel", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/renderer/GameRenderer;resetProjectionMatrix(Lorg/joml/Matrix4f;)V"))
    public void renderLevel(GameRenderer gr, Matrix4f matrix4f, float f, long l, PoseStack poseStack) {
        ExordiumModBase.instance.getDelayedRenderCallManager().setProjectionMatrix(matrix4f);
        resetProjectionMatrix(matrix4f);
    }
    
    @Shadow
    public abstract void resetProjectionMatrix(Matrix4f matrix4f);
    
}
