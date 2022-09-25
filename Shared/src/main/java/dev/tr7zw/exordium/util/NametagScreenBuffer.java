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

// TODO: Bit of a spaghetti logic. Should be cleaned up at some point
public class NametagScreenBuffer {

    private static final Minecraft minecraft = Minecraft.getInstance();
    private RenderTarget guiTarget = new TextureTarget(100, 100, true, false);
    private boolean needsNewData = true;
    private int requestedNewData = 0;
    private long nextFrame = System.currentTimeMillis();
    
    public NametagScreenBuffer(int cacheTime) {
        reset(cacheTime);
    }
    
    /**
     * Prepares the buffer for a new frame if the old one is too old or the size changed.
     * 
     * @param cacheTime
     */
    private void reset(int cacheTime) {
        guiTarget.setClearColor(0, 0, 0, 0);
        guiTarget.clear(false);
        nextFrame = System.currentTimeMillis() + cacheTime;
    }
    
    /**
     * @return true if ready for rendering
     */
    public boolean bind() {
        reset(1000/ExordiumModBase.instance.config.targetFPSNameTags);
        guiTarget.bindWrite(false);

        ExordiumModBase.instance.setTemporaryScreenOverwrite(guiTarget);
        return true;
    }
    
    public boolean acceptsData() {
        return needsNewData;
    }
    
    private void updateNeeds() {
        boolean forceRender = false;
        if (guiTarget.width != minecraft.getWindow().getWidth()
                || guiTarget.height != minecraft.getWindow().getHeight()) {
            guiTarget.resize(minecraft.getWindow().getWidth(), minecraft.getWindow().getHeight(), true);
            forceRender = true;
        }
        needsNewData = forceRender || System.currentTimeMillis() > nextFrame;
        if(needsNewData) {
            requestedNewData++;
        }else {
            requestedNewData = 0;
        }
    }

    public void bindEnd() {
        ExordiumModBase.instance.setTemporaryScreenOverwrite(null);
        guiTarget.unbindWrite();
        Minecraft.getInstance().getMainRenderTarget().bindWrite(true);
        needsNewData = false;
    }

    public void renderOverlay() {
        if(needsNewData && requestedNewData >= 2)return;
        int screenWidth = minecraft.getWindow().getGuiScaledWidth();
        int screenHeight = minecraft.getWindow().getGuiScaledHeight();
        renderTextureOverlay(guiTarget.getColorTextureId(), screenWidth, screenHeight);
        updateNeeds();
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
//        RenderSystem.depthMask(true);
//        RenderSystem.enableDepthTest();

    }
    
}
