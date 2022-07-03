package dev.tr7zw.fastergui.util;

import java.lang.ref.Cleaner;

import com.mojang.blaze3d.pipeline.RenderTarget;
import com.mojang.blaze3d.pipeline.TextureTarget;
import com.mojang.blaze3d.platform.GlStateManager;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.BufferBuilder;
import com.mojang.blaze3d.vertex.DefaultVertexFormat;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.Tesselator;
import com.mojang.blaze3d.vertex.VertexFormat;
import com.mojang.math.Matrix3f;
import com.mojang.math.Matrix4f;

import dev.tr7zw.fastergui.FasterGuiModBase;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.network.chat.Component;

public class NametagBufferRenderer {

    private static final Cleaner cleaner = Cleaner.create();
    private static final Minecraft minecraft = Minecraft.getInstance();
    private RenderTarget renderTarget;
    
    public NametagBufferRenderer() {

    }
    
    public void refreshImage(Component text, MultiBufferSource arg3, int light, boolean discrete) {
        int width = (int)(minecraft.font.width(text) * FasterGuiModBase.nametagSettings.bufferWidth);
        width = Math.max(300, width);
        int height = width;

        if(renderTarget == null) {
            renderTarget = new TextureTarget(width, height, false, false);
            renderTarget.setClearColor(0, 0, 0, 0);
            renderTarget.clear(false);
            cleaner.register(this, new State(renderTarget));
        }
        if(renderTarget.width != width || renderTarget.height != height) {
            renderTarget.resize(width, height, false);
        }
        System.out.println("Size: " + width);
        renderTarget.clear(false);
        renderNametagToBuffer(text, arg3, light, discrete);
        Minecraft.getInstance().getMainRenderTarget().bindWrite(true);
    }
    
    public void render(PoseStack poseStack, int light) {
        poseStack.pushPose();
        poseStack.translate(FasterGuiModBase.nametagSettings.offsetX , FasterGuiModBase.nametagSettings.offsetY, 0);
        //RenderSystem.enableDepthTest();
        RenderSystem.disableDepthTest();
        RenderSystem.enableBlend();
//        FasterGuiModBase.correctBlendMode();
        RenderSystem.blendFunc(GlStateManager.SourceFactor.ONE, GlStateManager.DestFactor.ONE_MINUS_SRC_ALPHA);
        RenderSystem.setShader(GameRenderer::getPositionTexShader);
        RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, 1.0F);
        RenderSystem.setShaderTexture(0, renderTarget.getColorTextureId());
        Tesselator tesselator = RenderSystem.renderThreadTesselator();
        BufferBuilder bufferbuilder = tesselator.getBuilder();
        float height = (int)FasterGuiModBase.nametagSettings.renderHeight;
        float width = (int)FasterGuiModBase.nametagSettings.renderWidth;
        Matrix4f pose = poseStack.last().pose();
        bufferbuilder.begin(VertexFormat.Mode.QUADS, DefaultVertexFormat.POSITION_TEX);
        bufferbuilder.vertex(pose, 0.0f, height, 0.01F).uv(0.0F, 0.0F).uv2(light).endVertex(); // 1
        bufferbuilder.vertex(pose, width, height, 0.01F).uv(1.0F, 0.0F).uv2(light).endVertex(); // 2
        bufferbuilder.vertex(pose, width, 0.0f, 0.01F).uv(1.0F, 1.0F).uv2(light).endVertex(); // 3
        bufferbuilder.vertex(pose, 0.0f, 0.0f, 0.01F).uv(0.0F, 1.0F).uv2(light).endVertex(); // 4
        tesselator.end();
        poseStack.popPose();
        FasterGuiModBase.correctBlendMode();
    }
    
    private void renderNametagToBuffer(Component text, MultiBufferSource mbs, int light, boolean discrete) {
        renderTarget.bindWrite(false);
        // cache the current render state
        Matrix4f tmp = RenderSystem.getProjectionMatrix();
        Matrix3f tmpI = RenderSystem.getInverseViewRotationMatrix();
        // set the renderstate to identity matrices
        RenderSystem.disableCull();
        RenderSystem.setInverseViewRotationMatrix(Matrix3f.createScaleMatrix(1, 1, 1));
        RenderSystem.setProjectionMatrix(Matrix4f.createTranslateMatrix(0, 0, 0));
        float scale = 1/FasterGuiModBase.nametagSettings.scaleSize;
        // matrix used for the text
        Matrix4f matrix4f = Matrix4f.createScaleMatrix(scale, -scale, scale);
        float f1 = minecraft.options.getBackgroundOpacity(0.25F);
        int j = (int) (f1 * 255.0F) << 24;
        Font font = minecraft.font;
        float f2 = (-font.width(text) / 2);
        font.drawInBatch(text, f2, 0, 553648127, false, matrix4f, mbs, discrete, j, light);
        if (discrete)
            font.drawInBatch(text, f2, 0, -1, false, matrix4f, mbs, false, 0, light);
        mbs.getBuffer(RenderType.armorGlint()); // force clear the vertex consumer
        // restore render state
        Minecraft.getInstance().getMainRenderTarget().bindWrite(true);
        RenderSystem.setProjectionMatrix(tmp);
        RenderSystem.setInverseViewRotationMatrix(tmpI);
        RenderSystem.enableCull();
    }

    static class State implements Runnable {

        private RenderTarget cleanableRenderTarget;
        
        State(RenderTarget guiTarget) {
            this.cleanableRenderTarget = guiTarget;
        }

        public void run() {
            RenderSystem.recordRenderCall(() -> {
                cleanableRenderTarget.destroyBuffers();
            });
        }
    }
    
}
