package dev.tr7zw.fastergui.mixin;

import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import com.mojang.blaze3d.pipeline.RenderTarget;
import com.mojang.blaze3d.pipeline.TextureTarget;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.BufferBuilder;
import com.mojang.blaze3d.vertex.DefaultVertexFormat;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.Tesselator;
import com.mojang.blaze3d.vertex.VertexFormat;

import dev.tr7zw.fastergui.FasterGuiModBase;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.resources.ResourceLocation;

@Mixin(Gui.class)
public class GuiMixin {
    
    @Shadow
    public static ResourceLocation VIGNETTE_LOCATION;
    @Shadow
    protected int screenWidth;
    @Shadow
    protected int screenHeight;
    @Shadow
    @Final
    private Minecraft minecraft;
    
    private RenderTarget guiTarget = new TextureTarget(100, 100, true, false);
    private long nextFrame = System.currentTimeMillis();
    
    
    @Inject(method = "render", at = @At(value="INVOKE", target = "Lnet/minecraft/client/Minecraft;getDeltaFrameTime()F"), cancellable = true)
    public void render(PoseStack arg, float g, CallbackInfo ci) {
        if(!FasterGuiModBase.instance.config.enabled) {
            return;
        }
        boolean forceRender = false;
        if(guiTarget.width != minecraft.getWindow().getWidth() || guiTarget.height != minecraft.getWindow().getHeight()) {
            guiTarget.resize(minecraft.getWindow().getWidth(), minecraft.getWindow().getHeight(), true);
            forceRender = true;
        }
        renderTextureOverlay(guiTarget.getColorTextureId());
        if(!forceRender && System.currentTimeMillis() < nextFrame) {
            ci.cancel();
            return;
        }
        guiTarget.setClearColor(0, 0, 0, 0);
        guiTarget.clear(false);
        guiTarget.bindWrite(false);
    }
    
    @Inject(method = "render", at = @At("TAIL"))
    public void renderEnd(PoseStack arg, float g, CallbackInfo ci) {
        if(!FasterGuiModBase.instance.config.enabled) {
            return;
        }
        guiTarget.unbindWrite();
        Minecraft.getInstance().getMainRenderTarget().bindWrite(true);
        // always just rendering last frame prevents double rendering due to the chat?
//        renderTextureOverlay(guiTarget.getColorTextureId());
        nextFrame = System.currentTimeMillis() + (1000/FasterGuiModBase.instance.config.targetFPSIngameGui);
    }

    protected void renderTextureOverlay(int textureid) {
        RenderSystem.enableDepthTest();
        RenderSystem.depthMask(true);
        RenderSystem.defaultBlendFunc();
        RenderSystem.setShader(GameRenderer::getPositionTexShader);
        RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, 1.0F);
        RenderSystem.setShaderTexture(0, textureid);
        Tesselator tesselator = Tesselator.getInstance();
        BufferBuilder bufferbuilder = tesselator.getBuilder();
        bufferbuilder.begin(VertexFormat.Mode.QUADS, DefaultVertexFormat.POSITION_TEX);
        bufferbuilder.vertex(0.0D, this.screenHeight, -90.0D).uv(0.0F, 0.0F).endVertex(); // 1
        bufferbuilder.vertex(this.screenWidth, this.screenHeight, -90.0D).uv(1.0F, 0.0F).endVertex(); // 2
        bufferbuilder.vertex(this.screenWidth, 0.0D, -90.0D).uv(1.0F, 1.0F).endVertex(); // 3
        bufferbuilder.vertex(0.0D, 0.0D, -90.0D).uv(0.0F, 1.0F).endVertex(); // 4
        tesselator.end();
        RenderSystem.depthMask(true);
        RenderSystem.enableDepthTest();
        RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, 1.0F);
    }

}
