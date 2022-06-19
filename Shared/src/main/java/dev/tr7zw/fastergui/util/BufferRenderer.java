package dev.tr7zw.fastergui.util;

import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import com.mojang.blaze3d.pipeline.RenderTarget;
import com.mojang.blaze3d.pipeline.TextureTarget;
import com.mojang.blaze3d.platform.GlStateManager;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.BufferBuilder;
import com.mojang.blaze3d.vertex.DefaultVertexFormat;
import com.mojang.blaze3d.vertex.Tesselator;
import com.mojang.blaze3d.vertex.VertexFormat;

import dev.tr7zw.fastergui.FasterGuiModBase;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.GameRenderer;

public class BufferRenderer {

    private static final Minecraft minecraft = Minecraft.getInstance();
    private RenderTarget guiTarget = new TextureTarget(100, 100, true, false);
    private long nextFrame = System.currentTimeMillis();
    private boolean isRendering = false;
    private boolean forceBlending = false;
    
    public BufferRenderer() {
        this(false);
    }
    
    public BufferRenderer(boolean forceBlending) {
        this.forceBlending = forceBlending;
    }

    public void render(CallbackInfo ci) {
        int screenWidth = minecraft.getWindow().getGuiScaledWidth();
        int screenHeight = minecraft.getWindow().getGuiScaledHeight();
        boolean forceRender = false;
        if (guiTarget.width != minecraft.getWindow().getWidth()
                || guiTarget.height != minecraft.getWindow().getHeight()) {
            guiTarget.resize(minecraft.getWindow().getWidth(), minecraft.getWindow().getHeight(), true);
            forceRender = true;
        }
        renderTextureOverlay(guiTarget.getColorTextureId(), screenWidth, screenHeight);
        if (!forceRender && System.currentTimeMillis() < nextFrame) {
            ci.cancel();
            return;
        }
        guiTarget.setClearColor(0, 0, 0, 0);
        guiTarget.clear(false);
        guiTarget.bindWrite(false);

        FasterGuiModBase.correctBlendMode();
        isRendering = true;
        if(forceBlending) {
            FasterGuiModBase.setForceBlend(true);
            FasterGuiModBase.setBlendBypass(false);
        }
    }

    public void renderEnd(int cacheTime) {
        guiTarget.unbindWrite();
        Minecraft.getInstance().getMainRenderTarget().bindWrite(true);
        nextFrame = System.currentTimeMillis() + cacheTime;
        isRendering = false;
        if(forceBlending) {
            FasterGuiModBase.setBlendBypass(false);
            FasterGuiModBase.setForceBlend(false);
        }
    }

    private void renderTextureOverlay(int textureid, int screenWidth, int screenHeight) {
        RenderSystem.disableDepthTest();
        RenderSystem.depthMask(false);
        RenderSystem.blendFunc(GlStateManager.SourceFactor.ONE, GlStateManager.DestFactor.ONE_MINUS_SRC_ALPHA);
        RenderSystem.setShader(GameRenderer::getPositionTexShader);
        RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, 1.0F);
        RenderSystem.setShaderTexture(0, textureid);
        Tesselator tesselator = Tesselator.getInstance();
        BufferBuilder bufferbuilder = tesselator.getBuilder();
        bufferbuilder.begin(VertexFormat.Mode.QUADS, DefaultVertexFormat.POSITION_TEX);
        bufferbuilder.vertex(0.0D, screenHeight, -90.0D).uv(0.0F, 0.0F).endVertex(); // 1
        bufferbuilder.vertex(screenWidth, screenHeight, -90.0D).uv(1.0F, 0.0F).endVertex(); // 2
        bufferbuilder.vertex(screenWidth, 0.0D, -90.0D).uv(1.0F, 1.0F).endVertex(); // 3
        bufferbuilder.vertex(0.0D, 0.0D, -90.0D).uv(0.0F, 1.0F).endVertex(); // 4
        tesselator.end();
        RenderSystem.depthMask(true);
        RenderSystem.enableDepthTest();
        RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, 1.0F);
    }

    public boolean isRendering() {
        return isRendering;
    }

}
