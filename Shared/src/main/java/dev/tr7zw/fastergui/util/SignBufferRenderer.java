package dev.tr7zw.fastergui.util;

import java.lang.ref.Cleaner;
import java.util.List;

import com.mojang.blaze3d.pipeline.RenderTarget;
import com.mojang.blaze3d.pipeline.TextureTarget;
import com.mojang.blaze3d.platform.GlStateManager;
import com.mojang.blaze3d.platform.NativeImage;
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
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.util.FormattedCharSequence;
import net.minecraft.world.item.DyeColor;
import net.minecraft.world.level.block.entity.SignBlockEntity;

public class SignBufferRenderer {

    private static final Cleaner cleaner = Cleaner.create();
    private static final Minecraft minecraft = Minecraft.getInstance();
    private RenderTarget guiTarget;
    
    public SignBufferRenderer(SignBlockEntity arg, MultiBufferSource arg3, int light) {
        guiTarget = new TextureTarget((int)FasterGuiModBase.signSettings.bufferWidth, (int)FasterGuiModBase.signSettings.bufferHeight, false, false);
//        guiTarget.resize((int)FasterGuiModBase.signSettings.bufferWidth, (int)FasterGuiModBase.signSettings.bufferHeight, false);
        guiTarget.setClearColor(0, 0, 0, 0);
        guiTarget.clear(false);
        cleaner.register(this, new State(guiTarget));
    }
    
    public void refreshImage(SignBlockEntity arg, MultiBufferSource arg3, int light) {
        guiTarget.clear(false);
        renderSignToBuffer(arg, arg3, light);
    }
    
    public void render(PoseStack poseStack, int light) {
        poseStack.pushPose();
        poseStack.translate(FasterGuiModBase.signSettings.offsetX , FasterGuiModBase.signSettings.offsetY, 0);
        RenderSystem.depthMask(true);
        RenderSystem.enableDepthTest();
        RenderSystem.enableBlend();
        RenderSystem.blendFunc(GlStateManager.SourceFactor.ONE, GlStateManager.DestFactor.ONE_MINUS_SRC_ALPHA);
        RenderSystem.setShader(GameRenderer::getPositionTexShader);
        RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, 1.0F);
        RenderSystem.setShaderTexture(0, guiTarget.getColorTextureId());
        Tesselator tesselator = RenderSystem.renderThreadTesselator();
        BufferBuilder bufferbuilder = tesselator.getBuilder();
        float height = (int)FasterGuiModBase.signSettings.renderHeight;
        float width = (int)FasterGuiModBase.signSettings.renderWidth;
        Matrix4f pose = poseStack.last().pose();
        bufferbuilder.begin(VertexFormat.Mode.QUADS, DefaultVertexFormat.POSITION_TEX);
        bufferbuilder.vertex(pose, 0.0f, height, 0.01F).uv(0.0F, 0.0F).uv2(light).endVertex(); // 1
        bufferbuilder.vertex(pose, width, height, 0.01F).uv(1.0F, 0.0F).uv2(light).endVertex(); // 2
        bufferbuilder.vertex(pose, width, 0.0f, 0.01F).uv(1.0F, 1.0F).uv2(light).endVertex(); // 3
        bufferbuilder.vertex(pose, 0.0f, 0.0f, 0.01F).uv(0.0F, 1.0F).uv2(light).endVertex(); // 4
        tesselator.end();
        poseStack.popPose();
    }
    
    private void renderSignToBuffer(SignBlockEntity arg, MultiBufferSource arg3, int light) {
        guiTarget.bindWrite(false);
        // cache the current render state
        Matrix4f tmp = RenderSystem.getProjectionMatrix();
        Matrix3f tmpI = RenderSystem.getInverseViewRotationMatrix();
        // set the renderstate to identity matrices
        RenderSystem.disableCull();
        RenderSystem.setInverseViewRotationMatrix(Matrix3f.createScaleMatrix(1, 1, 1));
        RenderSystem.setProjectionMatrix(Matrix4f.createTranslateMatrix(0, 0, 0));
        float scale = 1/FasterGuiModBase.signSettings.scaleSize;
        // matrix used for the text
        Matrix4f matrix4f = Matrix4f.createScaleMatrix(scale, -scale, scale);
        int n;
        boolean bl;
        int o;
        int l = getDarkColor(arg);
        FormattedCharSequence[] formattedCharSequences = arg
                .getRenderMessages(Minecraft.getInstance().isTextFilteringEnabled(), arg2 -> {
                    List<FormattedCharSequence> list = minecraft.font.split(arg2, 90);
                    return list.isEmpty() ? FormattedCharSequence.EMPTY : list.get(0);
                });
        if (arg.hasGlowingText()) {
            n = arg.getColor().getTextColor();
            bl = true;//isOutlineVisible(arg, n);
            o = 15728880;
        } else {
            n = l;
            bl = false;
            o = light;
        }
        for (int p = 0; p < 4; p++) {
            FormattedCharSequence formattedCharSequence = formattedCharSequences[p];
            float q = (-minecraft.font.width(formattedCharSequence) / 2);
            if (bl) {
                minecraft.font.drawInBatch8xOutline(formattedCharSequence,-28 +  q, (p * 10 - 20), n, l, matrix4f, arg3,
                        o);
            } else {
                minecraft.font.drawInBatch(formattedCharSequence, (-28 + q), (p * 10 - 20), n, false, matrix4f, arg3,
                        false, 0, o);
            }
        }
        arg3.getBuffer(RenderType.armorGlint()); // force clear the vertex consumer
        // restore renderlogic
        Minecraft.getInstance().getMainRenderTarget().bindWrite(true);
        RenderSystem.setProjectionMatrix(tmp);
        RenderSystem.setInverseViewRotationMatrix(tmpI);
    }
    
    private static int getDarkColor(SignBlockEntity arg) {
        int i = arg.getColor().getTextColor();
        double d = 0.4D;
        int j = (int) (NativeImage.getR(i) * d);
        int k = (int) (NativeImage.getG(i) * d);
        int l = (int) (NativeImage.getB(i) * d);
        if (i == DyeColor.BLACK.getTextColor() && arg.hasGlowingText())
            return -988212;
        return NativeImage.combine(0, l, k, j);
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
