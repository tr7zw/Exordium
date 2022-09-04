package dev.tr7zw.fastergui.mixin;

import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.At.Shift;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import dev.tr7zw.fastergui.FasterGuiModBase;
import dev.tr7zw.fastergui.util.NametagScreenBuffer;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.GameRenderer;

@Mixin(value = GameRenderer.class, priority = 500) //needs to be lower to cancel Architectury for REI
public class GameRendererMixinLow {

    @Shadow
    @Final
    private Minecraft minecraft;
    
    @Inject(method = "render(FJZ)V", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/gui/screens/Screen;render(Lcom/mojang/blaze3d/vertex/PoseStack;IIF)V", ordinal = 0), cancellable = true)
    public void renderScreenPre(float tickDelta, long startTime, boolean tick, CallbackInfo ci) {
        if(!FasterGuiModBase.instance.config.enabledScreens || minecraft.level == null) {
            return;
        }

        boolean cancel = FasterGuiModBase.instance.getScreenBufferRenderer().render();
        if(cancel)
            ci.cancel();
    }
    
    @Inject(method = "render(FJZ)V", at = @At(value = "INVOKE", target = "Lcom/mojang/blaze3d/platform/Lighting;setupFor3DItems()V", ordinal = 0, shift = Shift.AFTER))
    public void renderLevel(float tickDelta, long startTime, boolean tick, CallbackInfo info) {
        NametagScreenBuffer buffer = FasterGuiModBase.instance.getNameTagScreenBuffer();
        buffer.renderOverlay();
        buffer.reset(1000/FasterGuiModBase.instance.config.targetFPSNameTags);
        Minecraft.getInstance().getMainRenderTarget().bindWrite(true);
    }
    
    @Inject(method = "render(FJZ)V", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/renderer/GameRenderer;renderLevel(FJLcom/mojang/blaze3d/vertex/PoseStack;)V", ordinal = 0, shift = Shift.AFTER))
    public void postWorldRender(float tickDelta, long startTime, boolean tick, CallbackInfo ci) {
        FasterGuiModBase.instance.getDelayedRenderCallManager().execRenderCalls();
    }
    
}
