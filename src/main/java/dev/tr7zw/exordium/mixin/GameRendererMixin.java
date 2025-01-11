package dev.tr7zw.exordium.mixin;

import net.minecraft.client.renderer.*;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.resources.ResourceManager;
import net.minecraft.util.profiling.ProfilerFiller;
import org.jetbrains.annotations.Nullable;
import org.joml.Matrix4f;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import com.mojang.blaze3d.vertex.DefaultVertexFormat;

import dev.tr7zw.exordium.ExordiumModBase;
//#if MC >= 12102

@Mixin(ShaderManager.class)
public abstract class GameRendererMixin {
    @Shadow
    public abstract @Nullable CompiledShaderProgram getProgram(ShaderProgram shaderProgram);

    @Inject(method = "apply(Lnet/minecraft/client/renderer/ShaderManager$Configs;Lnet/minecraft/server/packs/resources/ResourceManager;Lnet/minecraft/util/profiling/ProfilerFiller;)V", at = @At("TAIL"))
    private void reloadCustomShader(ShaderManager.Configs configs, ResourceManager resourceManager,
            ProfilerFiller profilerFiller, CallbackInfo ci) {
        ResourceLocation resourceLocation = ResourceLocation.withDefaultNamespace("core/position_multi_tex");
        CompiledShaderProgram shaderProgram = getProgram(
                new ShaderProgram(resourceLocation, DefaultVertexFormat.POSITION_TEX, ShaderDefines.EMPTY));
        if (shaderProgram != null)
            ExordiumModBase.instance.getCustomShaderManager().registerShaderInstance(shaderProgram);
        else
            throw new RuntimeException("Unable to load Exordium shader, see above error.");
    }
}

//#else
//$$import net.minecraft.server.packs.resources.ResourceProvider;
//$$import net.minecraft.client.renderer.ShaderInstance;
//$$import java.io.IOException;
//$$@Mixin(value = GameRenderer.class, priority = 500) // needs to be lower to cancel Architectury for REI
//$$public abstract class GameRendererMixin {
//$$
//$$//    @Redirect(method = "renderLevel", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/renderer/GameRenderer;resetProjectionMatrix(Lorg/joml/Matrix4f;)V"))
//$$//    public void renderLevel(GameRenderer gr, Matrix4f matrix4f, float f, long l) {
//$$//        resetProjectionMatrix(matrix4f);
//$$//    }
//$$
//$$    @Shadow
//$$    public abstract void resetProjectionMatrix(Matrix4f matrix4f);
//$$
//$$    @Inject(method = "reloadShaders", at = @At("TAIL"))
//$$    public void reloadShaders(ResourceProvider resourceProvider, CallbackInfo ci) {
//$$        try {
//$$            ExordiumModBase.instance.getCustomShaderManager().registerShaderInstance(
//$$                    new ShaderInstance(resourceProvider, "position_multi_tex", DefaultVertexFormat.POSITION_TEX));
//$$        } catch (IOException e) {
//$$            throw new RuntimeException("Unable to load Exordium Shader", e);
//$$        }
//$$    }
//$$
//$$
//$$}
//#endif