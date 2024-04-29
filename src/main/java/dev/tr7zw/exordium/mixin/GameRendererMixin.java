package dev.tr7zw.exordium.mixin;

import com.mojang.blaze3d.vertex.DefaultVertexFormat;
import com.mojang.blaze3d.vertex.PoseStack;
import dev.tr7zw.exordium.ExordiumModBase;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.client.renderer.ShaderInstance;
import net.minecraft.server.packs.resources.ResourceProvider;
import org.joml.Matrix4f;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.At.Shift;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.io.IOException;

@Mixin(value = GameRenderer.class, priority = 500) // needs to be lower to cancel Architectury for REI
public abstract class GameRendererMixin {

    @Inject(method = "render(FJZ)V", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/Minecraft;getWindow()Lcom/mojang/blaze3d/platform/Window;", ordinal = 0, shift = Shift.AFTER))
    public void postWorldRender(float tickDelta, long startTime, boolean tick, CallbackInfo ci) {
        ExordiumModBase.instance.getDelayedRenderCallManager().execRenderCalls();
    }

    @Redirect(method = "renderLevel", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/renderer/GameRenderer;resetProjectionMatrix(Lorg/joml/Matrix4f;)V"))
    public void renderLevel(GameRenderer gr, Matrix4f matrix4f, float f, long l) {
        ExordiumModBase.instance.getDelayedRenderCallManager().setProjectionMatrix(matrix4f);
        resetProjectionMatrix(matrix4f);
    }

    @Shadow
    public abstract void resetProjectionMatrix(Matrix4f matrix4f);

    @Inject(method = "reloadShaders", at = @At("TAIL"))
    public void reloadShaders(ResourceProvider resourceProvider, CallbackInfo ci) {
        try {
            ExordiumModBase.instance.getCustomShaderManager().registerShaderInstance(
                    new ShaderInstance(resourceProvider, "position_multi_tex", DefaultVertexFormat.POSITION_TEX));
        } catch (IOException e) {
            throw new RuntimeException("Unable to load Exordium Shader", e);
        }
    }

}