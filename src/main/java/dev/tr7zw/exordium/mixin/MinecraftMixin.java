package dev.tr7zw.exordium.mixin;

import dev.tr7zw.transition.mc.*;
import net.minecraft.client.gui.screens.*;
import org.spongepowered.asm.mixin.*;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.*;

import com.mojang.blaze3d.pipeline.RenderTarget;

import dev.tr7zw.exordium.ExordiumModBase;
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

    @Shadow
    private static Minecraft instance;
    private boolean naggedThisSession = false;

    @Inject(method = "getMainRenderTarget", at = @At("HEAD"), cancellable = true)
    public void getMainRenderTarget(CallbackInfoReturnable<RenderTarget> ci) {
        if (ExordiumModBase.instance == null) {
            return; // Mod not initialized yet, so no temporary screen overwrite
        }
        RenderTarget target = ExordiumModBase.instance.getTemporaryScreenOverwrite();
        if (target != null) {
            ci.setReturnValue(target);
            ci.cancel();
        }
    }

    //    @Inject(at = @At(value = "INVOKE", target = "Lnet/minecraft/client/Minecraft;allowsMultiplayer()Z", shift = Shift.BEFORE), method = "Lnet/minecraft/client/Minecraft;<init>(Lnet/minecraft/client/main/GameConfig;)V")
    //    private void init(GameConfig config, CallbackInfo info) {
    //       System.loadLibrary("renderdoc");
    //    }

    @Inject(method = "setScreen", at = @At("HEAD"), cancellable = true)
    public void setScreen(Screen screen, CallbackInfo ci) {
        if (!naggedThisSession && !ExordiumModBase.instance.config.iKnowThisIsNotAnActivelyDevelopedMod
                && screen != null && screen instanceof TitleScreen) {
            // Only nag once per session
            naggedThisSession = true;
            Minecraft.getInstance().setScreen(new ConfirmScreen(b -> {
                if (b) {
                    ExordiumModBase.instance.config.iKnowThisIsNotAnActivelyDevelopedMod = true;
                    ExordiumModBase.instance.writeConfig();
                }
                Minecraft.getInstance().setScreen(screen);
            }, ComponentProvider.translatable("text.exordium.warning"),
                    ComponentProvider.translatable("text.exordium.warning_desc")));
            ci.cancel();
        }
    }

}
