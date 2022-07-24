package dev.tr7zw.fastergui.util;

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

public class NametagScreenBuffer {

    private static final Minecraft minecraft = Minecraft.getInstance();
    private RenderTarget guiTarget = new TextureTarget(100, 100, true, false);
    private boolean containsTags = false;
    private boolean acceptsTags = true;
    private boolean isRendering = false;
    private long nextFrame = System.currentTimeMillis();
    
    public NametagScreenBuffer(int cacheTime) {
        reset(cacheTime);
    }
    
    /**
     * Prepares the buffer for a new frame if the old one is too old or the size changed.
     * 
     * @param cacheTime
     */
    public void reset(int cacheTime) {

        boolean forceRender = false;
        if (guiTarget.width != minecraft.getWindow().getWidth()
                || guiTarget.height != minecraft.getWindow().getHeight()) {
            guiTarget.resize(minecraft.getWindow().getWidth(), minecraft.getWindow().getHeight(), true);
            forceRender = true;
        }
        if (forceRender || System.currentTimeMillis() > nextFrame) {
            if(forceRender || containsTags) { // only refresh the buffer if there was something in it
                guiTarget.setClearColor(0, 0, 0, 0);
                guiTarget.clear(false);
            }
            nextFrame = System.currentTimeMillis() + cacheTime;
            acceptsTags = true;
            containsTags = false;
        }
    }
    
    /**
     * @return true if ready for rendering
     */
    public boolean bind() {
        if(!acceptsTags) {
            return false;
        }
        containsTags = true;
        guiTarget.bindWrite(false);

        isRendering = true;
        FasterGuiModBase.instance.setTemporaryScreenOverwrite(guiTarget);
        return true;
    }

    public void bindEnd() {
        if(!isRendering)return;
        FasterGuiModBase.instance.setTemporaryScreenOverwrite(null);
        guiTarget.unbindWrite();
        Minecraft.getInstance().getMainRenderTarget().bindWrite(true);
        isRendering = false;
    }

    public void renderOverlay() {
        int screenWidth = minecraft.getWindow().getGuiScaledWidth();
        int screenHeight = minecraft.getWindow().getGuiScaledHeight();
        renderTextureOverlay(guiTarget.getColorTextureId(), screenWidth, screenHeight);
    }
    
    private void renderTextureOverlay(int textureid, int screenWidth, int screenHeight) {
        if(!containsTags) {
            return; // never was bound, nothing to render
        }
        acceptsTags = false;
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

    public boolean isRendering() {
        return isRendering;
    }
    
}
