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
import com.mojang.math.Matrix4f;

import net.minecraft.client.Camera;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.util.FormattedCharSequence;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.DyeColor;
import net.minecraft.world.level.block.entity.SignBlockEntity;
import net.minecraft.world.level.material.FogType;

public class SignBufferRenderer {

    private static final Cleaner cleaner = Cleaner.create();
    private static final Minecraft minecraft = Minecraft.getInstance();
    private RenderTarget guiTarget;
    
    public SignBufferRenderer(SignBlockEntity arg, MultiBufferSource arg3) {
        guiTarget = new TextureTarget(260, 180, false, false);
        guiTarget.resize(260, 180, false);
        guiTarget.setClearColor(0, 0, 0, 0);
        guiTarget.clear(false);
        cleaner.register(this, new State(guiTarget));
        renderSignToBuffer(arg, arg3);
        System.out.println("Rendered to " + guiTarget.frameBufferId);
    }
    
    public void render(PoseStack poseStack) {
        RenderSystem.depthMask(true);
        RenderSystem.enableDepthTest();
        RenderSystem.enableBlend();
        RenderSystem.blendFunc(GlStateManager.SourceFactor.ONE, GlStateManager.DestFactor.ONE_MINUS_SRC_ALPHA);
        RenderSystem.setShader(GameRenderer::getPositionTexShader);
        RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, 1.0F);
        RenderSystem.setShaderTexture(0, guiTarget.getColorTextureId());
        Tesselator tesselator = RenderSystem.renderThreadTesselator();
        BufferBuilder bufferbuilder = tesselator.getBuilder();
        float height = 260;
        float width = 180;
        Matrix4f pose = poseStack.last().pose();
        bufferbuilder.begin(VertexFormat.Mode.QUADS, DefaultVertexFormat.POSITION_TEX);
        bufferbuilder.vertex(pose, 0.0f, height, 0.01F).uv(0.0F, 0.0F).endVertex(); // 1
        bufferbuilder.vertex(pose, width, height, 0.01F).uv(1.0F, 0.0F).endVertex(); // 2
        bufferbuilder.vertex(pose, width, 0.0f, 0.01F).uv(1.0F, 1.0F).endVertex(); // 3
        bufferbuilder.vertex(pose, 0.0f, 0.0f, 0.01F).uv(0.0F, 1.0F).endVertex(); // 4
//        BufferUploader.draw(bufferbuilder.end());
        tesselator.end();
    }
    
    private void renderSignToBuffer(SignBlockEntity arg, MultiBufferSource arg3) {
        guiTarget.bindWrite(false);
        Matrix4f tmp = RenderSystem.getProjectionMatrix();
        // TODO: Burn this code and throw it into the depths of hell
        RenderSystem.setProjectionMatrix(minecraft.gameRenderer.getProjectionMatrix(getFov(minecraft.gameRenderer.getMainCamera(), minecraft.getDeltaFrameTime())));
        Matrix4f matrix4f = Matrix4f.orthographic(100, -100, 1000.0F, 3000.0F);
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
            o = 15728880;//i;
        }
        for (int p = 0; p < 4; p++) {
            FormattedCharSequence formattedCharSequence = formattedCharSequences[p];
            float q = (-minecraft.font.width(formattedCharSequence) / 2);
            if (bl) {
                minecraft.font.drawInBatch8xOutline(formattedCharSequence,-28 +  q, (p * 10 - 20), n, l, matrix4f, arg3,
                        o);
            } else {
                minecraft.font.drawInBatch(formattedCharSequence, -28 + q, (p * 10 - 20), n, false, matrix4f, arg3,
                        false, 0, o);
            }
        }
        arg3.getBuffer(RenderType.armorGlint()); // force clear the buffer
        Minecraft.getInstance().getMainRenderTarget().bindWrite(true);
        RenderSystem.setProjectionMatrix(tmp);
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
    
    private double getFov(Camera camera, float f) {
        double d = 70.0D;
        if (camera.getEntity() instanceof LivingEntity && ((LivingEntity) camera.getEntity()).isDeadOrDying()) {
            float g = Math.min(((LivingEntity) camera.getEntity()).deathTime + f, 20.0F);
            d /= ((1.0F - 500.0F / (g + 500.0F)) * 2.0F + 1.0F);
        }
        FogType fogType = camera.getFluidInCamera();
        if (fogType == FogType.LAVA || fogType == FogType.WATER)
            d *= Mth.lerp((minecraft.options.fovEffectScale().get()).doubleValue(), 1.0D,
                    0.8571428656578064D);
        return d;
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
