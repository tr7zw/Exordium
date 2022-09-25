package dev.tr7zw.exordium.mixin;

import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import dev.tr7zw.exordium.ExordiumModBase;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.GameRenderer;

@Mixin(value = GameRenderer.class, priority = 1500) //needs to be higher to also capture Architectury for REI
public class GameRendererMixinHigh {

    @Shadow
    @Final
    private Minecraft minecraft;

    @Inject(method = "render(FJZ)V", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/gui/screens/Screen;render(Lcom/mojang/blaze3d/vertex/PoseStack;IIF)V", shift = At.Shift.AFTER, ordinal = 0))
    public void renderScreenPost(float tickDelta, long startTime, boolean tick, CallbackInfo ci) {
        if(!ExordiumModBase.instance.config.enabledScreens || minecraft.level == null) {
            return;
        }
        ExordiumModBase.instance.getScreenBufferRenderer().renderEnd(1000/ExordiumModBase.instance.config.targetFPSIngameScreens);
    }

}
