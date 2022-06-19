package dev.tr7zw.fastergui.mixin;

import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import dev.tr7zw.fastergui.FasterGuiModBase;
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

        FasterGuiModBase.instance.getScreenBufferRenderer().render(ci);
    }
    
}
