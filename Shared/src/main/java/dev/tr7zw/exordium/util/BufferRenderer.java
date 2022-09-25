package dev.tr7zw.exordium.util;

import com.mojang.blaze3d.pipeline.RenderTarget;
import com.mojang.blaze3d.pipeline.TextureTarget;
import com.mojang.blaze3d.platform.GlStateManager;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.BufferBuilder;
import com.mojang.blaze3d.vertex.DefaultVertexFormat;
import com.mojang.blaze3d.vertex.Tesselator;
import com.mojang.blaze3d.vertex.VertexFormat;

import dev.tr7zw.exordium.ExordiumModBase;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.GameRenderer;

public class BufferRenderer {

    private static final Minecraft minecraft = Minecraft.getInstance();
    private RenderTarget guiTarget = new TextureTarget(100, 100, true, false);
    private long nextFrame = System.currentTimeMillis();
    private int guiScale = 0;
    private boolean isRendering = false;
    private boolean forceBlending = false;
    
    public BufferRenderer() {
        this(false);
    }
    
    public BufferRenderer(boolean forceBlending) {
        this.forceBlending = forceBlending;
    }

    public boolean render() {
        int screenWidth = minecraft.getWindow().getGuiScaledWidth();
        int screenHeight = minecraft.getWindow().getGuiScaledHeight();
        boolean forceRender = false;
        if (guiTarget.width != minecraft.getWindow().getWidth()
                || guiTarget.height != minecraft.getWindow().getHeight() || minecraft.options.guiScale != guiScale) {
            guiTarget.resize(minecraft.getWindow().getWidth(), minecraft.getWindow().getHeight(), true);
            guiScale = minecraft.options.guiScale;
            forceRender = true;
        }
        if (!forceRender && System.currentTimeMillis() < nextFrame) {
            renderTextureOverlay(guiTarget.getColorTextureId(), screenWidth, screenHeight);
            return true;
        }
        guiTarget.setClearColor(0, 0, 0, 0);
        guiTarget.clear(false);
        guiTarget.bindWrite(false);

        ExordiumModBase.correctBlendMode();
        isRendering = true;
        ExordiumModBase.instance.setTemporaryScreenOverwrite(guiTarget);
        if(forceBlending) {
            ExordiumModBase.setForceBlend(true);
            ExordiumModBase.setBlendBypass(false);
            ExordiumModBase.setBypassTurnoff(0);
        }
        return false;
    }

    public void renderEnd(int cacheTime) {
        guiTarget.unbindWrite();
        ExordiumModBase.instance.setTemporaryScreenOverwrite(null);
        Minecraft.getInstance().getMainRenderTarget().bindWrite(true);
        nextFrame = System.currentTimeMillis() + cacheTime;
        isRendering = false;
        if(forceBlending) {
            ExordiumModBase.setBlendBypass(false);
            ExordiumModBase.setForceBlend(false);
        }
        int screenWidth = minecraft.getWindow().getGuiScaledWidth();
        int screenHeight = minecraft.getWindow().getGuiScaledHeight();
        renderTextureOverlay(guiTarget.getColorTextureId(), screenWidth, screenHeight);
    }

    private void renderTextureOverlay(int textureid, int screenWidth, int screenHeight) {
        RenderSystem.disableDepthTest();
        RenderSystem.depthMask(false);
        RenderSystem.enableBlend();
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
