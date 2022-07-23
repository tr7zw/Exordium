package dev.tr7zw.fastergui.mixin;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import com.mojang.blaze3d.pipeline.RenderTarget;

import dev.tr7zw.fastergui.FasterGuiModBase;
import net.minecraft.client.Minecraft;

/**
 * While rendering the hud/screen, other mods might also use custom render
 * targets. This Mixin ensures that they switch back to the buffer from this mod
 * instead of the vanilla screen.
 * 
 * @author tr7zw
 *
 */
@Mixin(Minecraft.class)
public class MinecraftMixin {

    @Inject(method = "getMainRenderTarget", at = @At("HEAD"), cancellable = true)
    public void getMainRenderTarget(CallbackInfoReturnable<RenderTarget> ci) {
        RenderTarget target = FasterGuiModBase.instance.getTemporaryScreenOverwrite();
        if (target != null) {
            ci.setReturnValue(target);
            ci.cancel();
        }
    }

}
