package dev.tr7zw.fastergui.util;

import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import com.mojang.blaze3d.pipeline.RenderTarget;
import com.mojang.blaze3d.pipeline.TextureTarget;
import com.mojang.blaze3d.platform.GlStateManager;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.math.Vector3f;

import dev.tr7zw.fastergui.FasterGuiModBase;
import dev.tr7zw.fastergui.util.Model.Vector2f;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.GameRenderer;

public class BufferRenderer {

    private static final Minecraft minecraft = Minecraft.getInstance();
    private static Model model = null;
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

    private static void refreshModel(int screenWidth, int screenHeight){
        if(model != null) {
            model.close();
        }

        Vector3f[] modelData = new Vector3f[]{
            new Vector3f(0.0f, screenHeight, -90.0f),
            new Vector3f(screenWidth, screenHeight, -90.0F),
            new Vector3f(screenWidth, 0.0F, -90.0F),
            new Vector3f(0.0F, 0.0F, -90.0F),
        };
        Vector2f[] uvData = new Vector2f[]{
            new Vector2f(0.0f, 0.0f),
            new Vector2f(1.0f, 0.0f),
            new Vector2f(1.0f, 1.0f),
            new Vector2f(0.0f, 1.0f),
        };
        model = new Model(modelData, uvData);
    }
    
    public void render(CallbackInfo ci) {
        int screenWidth = minecraft.getWindow().getGuiScaledWidth();
        int screenHeight = minecraft.getWindow().getGuiScaledHeight();
        boolean forceRender = false;
        if (guiTarget.width != minecraft.getWindow().getWidth()
                || guiTarget.height != minecraft.getWindow().getHeight()) {
            guiTarget.resize(minecraft.getWindow().getWidth(), minecraft.getWindow().getHeight(), true);
            refreshModel(screenWidth, screenHeight);
            forceRender = true;
        }
        if(model == null) {
            refreshModel(screenWidth, screenHeight);
        }
        if (!forceRender && System.currentTimeMillis() < nextFrame) {
            renderTextureOverlay(guiTarget.getColorTextureId(), screenWidth, screenHeight);
            ci.cancel();
            return;
        }
        guiTarget.setClearColor(0, 0, 0, 0);
        guiTarget.clear(false);
        guiTarget.bindWrite(false);

        FasterGuiModBase.correctBlendMode();
        isRendering = true;
        FasterGuiModBase.instance.setTemporaryScreenOverwrite(guiTarget);
        if(forceBlending) {
            FasterGuiModBase.setForceBlend(true);
            FasterGuiModBase.setBlendBypass(false);
            FasterGuiModBase.setBypassTurnoff(0);
        }
    }

    public void renderEnd(int cacheTime) {
        guiTarget.unbindWrite();
        FasterGuiModBase.instance.setTemporaryScreenOverwrite(null);
        Minecraft.getInstance().getMainRenderTarget().bindWrite(true);
        nextFrame = System.currentTimeMillis() + cacheTime;
        isRendering = false;
        if(forceBlending) {
            FasterGuiModBase.setBlendBypass(false);
            FasterGuiModBase.setForceBlend(false);
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
        model.draw(RenderSystem.getModelViewMatrix());
        RenderSystem.depthMask(true);
        RenderSystem.enableDepthTest();
        RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, 1.0F);
    }

    public boolean isRendering() {
        return isRendering;
    }

}
