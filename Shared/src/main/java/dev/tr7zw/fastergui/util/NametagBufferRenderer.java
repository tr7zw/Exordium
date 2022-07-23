package dev.tr7zw.fastergui.util;

import java.lang.ref.Cleaner;

import com.mojang.blaze3d.pipeline.RenderTarget;
import com.mojang.blaze3d.pipeline.TextureTarget;
import com.mojang.blaze3d.platform.GlStateManager;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Matrix3f;
import com.mojang.math.Matrix4f;
import com.mojang.math.Vector3f;

import dev.tr7zw.fastergui.FasterGuiModBase;
import dev.tr7zw.fastergui.util.Model.Vector2f;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.network.chat.Component;

public class NametagBufferRenderer {

    private static final Cleaner cleaner = Cleaner.create();
    private static final Minecraft minecraft = Minecraft.getInstance();
    private RenderTarget renderTargetHidden;
    private RenderTarget renderTargetVisible;
    private Model model;
    private int textwidth = 0;

    private void initializeModel() {
        if(model != null) {
            model.close();
        }
        float height = (int)FasterGuiModBase.nametagSettings.renderHeight;
        
        Vector3f[] modelData = new Vector3f[]{
            new Vector3f(0.0f, height, 0.01F),
            new Vector3f(textwidth, height, 0.01F),
            new Vector3f(textwidth, 0.0f, 0.01F),
            new Vector3f(0.0f, 0.0f, 0.01F),
        };
        Vector2f[] uvData = new Vector2f[]{
            new Vector2f(0.0f, 0.0f),
            new Vector2f(1.0f, 0.0f),
            new Vector2f(1.0f, 1.0f),
            new Vector2f(0.0f, 1.0f),
        };
        model = new Model(modelData, uvData);
        cleaner.register(this, new ModelCleaner(model));
    }

    
    public void refreshImage(Component text, MultiBufferSource arg3, int light) {
        textwidth = minecraft.font.width(text);
        if(textwidth <= 0) { // fail save and quickly
            // the cleaner will handle this
            renderTargetHidden = null;
            renderTargetVisible = null;
            return;
        }
        
        arg3.getBuffer(RenderType.endGateway()); // force clear the vertex consumer

        int width = (int)(textwidth * FasterGuiModBase.nametagSettings.bufferWidth);
        int height = (int) FasterGuiModBase.nametagSettings.bufferHeight;

        if(renderTargetHidden == null) {
            renderTargetHidden = setupTexture(width, height);
            renderTargetVisible = setupTexture(width, height);
            initializeModel();
        }
        if(renderTargetHidden.width != width || renderTargetHidden.height != height) {
            renderTargetHidden.resize(width, height, false);
            renderTargetVisible.resize(width, height, false);
            initializeModel();
        }

        renderTargetHidden.clear(false);
        renderTargetVisible.clear(false);
        renderNametagToBuffer(renderTargetVisible, text, arg3, light, false);
        renderNametagToBuffer(renderTargetHidden, text, arg3, light, true);
        Minecraft.getInstance().getMainRenderTarget().bindWrite(true);
    }
    
    private RenderTarget setupTexture(int width, int height) {
        RenderTarget target = new TextureTarget(width, height, false, false);
        target.setClearColor(0, 0, 0, 0);
        target.clear(false);
        cleaner.register(this, new RenderTargetCleaner(target));
        return target;
    }
    
    public void render(PoseStack poseStack, int light, boolean hidden, boolean depthTest) {
        if(textwidth <= 0 || renderTargetHidden == null || renderTargetVisible == null)return; // no need to render 0 width
        RenderTarget renderTarget = hidden ? renderTargetHidden : renderTargetVisible;
        poseStack.pushPose();
        poseStack.translate(-textwidth/2f, 0, 0);
        RenderSystem.enableBlend();
        FasterGuiModBase.correctBlendMode();
        RenderSystem.setShader(GameRenderer::getPositionTexShader);
        if(depthTest) {
            RenderSystem.enableDepthTest();
        } else {
            RenderSystem.disableDepthTest();
        }
        RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, 1.0F);
        RenderSystem.setShaderTexture(0, renderTarget.getColorTextureId());
        model.draw(poseStack.last().pose());
        poseStack.popPose();
        FasterGuiModBase.correctBlendMode();
        RenderSystem.enableDepthTest();
    }
    
    private void renderNametagToBuffer(RenderTarget renderTarget, Component text, MultiBufferSource mbs, int light, boolean sneaking) {
        mbs.getBuffer(RenderType.endGateway()); // force clear the vertex consumer
        renderTarget.bindWrite(false);
        // cache the current render state
        Matrix4f tmp = RenderSystem.getProjectionMatrix();
        Matrix3f tmpI = RenderSystem.getInverseViewRotationMatrix();
        // set the renderstate to identity matrices
        RenderSystem.setInverseViewRotationMatrix(Matrix3f.createScaleMatrix(1, 1, 1));
        RenderSystem.setProjectionMatrix(Matrix4f.createTranslateMatrix(0, 0, 0));
        // other setup
        RenderSystem.disableCull();
        RenderSystem.enableBlend();
        RenderSystem.blendFuncSeparate(GlStateManager.SourceFactor.SRC_ALPHA, GlStateManager.DestFactor.ONE_MINUS_SRC_ALPHA, GlStateManager.SourceFactor.ONE, GlStateManager.DestFactor.ONE_MINUS_SRC_ALPHA);
        float scale = 1/FasterGuiModBase.nametagSettings.scaleSize;
        // matrix used for the text
        Matrix4f matrix4f = Matrix4f.createScaleMatrix(FasterGuiModBase.nametagSettings.scaleSize/renderTarget.width, FasterGuiModBase.nametagSettings.heightscale, scale);
        float f1 = minecraft.options.getBackgroundOpacity(0.25F);
        int j = (int) (f1 * 255f) << 24;
        Font font = minecraft.font;
        float f2 = (-font.width(text) / 2);
        if(sneaking) {
            font.drawInBatch(text, f2, 0, 1627389951, false, matrix4f, mbs, true, j, light);
        }else {
            font.drawInBatch(text, f2, 0, -1, false, matrix4f, mbs, false, j, light);
        }
        mbs.getBuffer(RenderType.endGateway()); // force clear the vertex consumer
        // restore render state
        RenderSystem.setProjectionMatrix(tmp);
        RenderSystem.setInverseViewRotationMatrix(tmpI);
        RenderSystem.enableCull();
    }

    static class RenderTargetCleaner implements Runnable {

        private RenderTarget cleanableRenderTarget;
        
        RenderTargetCleaner(RenderTarget renderTarget) {
            this.cleanableRenderTarget = renderTarget;
        }

        public void run() {
            RenderSystem.recordRenderCall(() -> {
                cleanableRenderTarget.destroyBuffers();
            });
        }
    }
    
    static class ModelCleaner implements Runnable {

        private Model cleanableModel;
        
        ModelCleaner(Model model) {
            this.cleanableModel = model;
        }

        public void run() {
            RenderSystem.recordRenderCall(() -> {
                cleanableModel.close();
            });
        }
    }
    
}
