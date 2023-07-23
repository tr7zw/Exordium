package dev.tr7zw.exordium.util;

import java.lang.ref.Cleaner;
import java.util.List;

import org.joml.Matrix3f;
import org.joml.Matrix4f;
import org.joml.Vector3f;

import com.mojang.blaze3d.pipeline.RenderTarget;
import com.mojang.blaze3d.pipeline.TextureTarget;
import com.mojang.blaze3d.platform.GlStateManager;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.Tesselator;

import dev.tr7zw.exordium.ExordiumModBase;
import dev.tr7zw.exordium.util.Model.Vector2f;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.util.FastColor;
import net.minecraft.util.FormattedCharSequence;
import net.minecraft.world.item.DyeColor;
import net.minecraft.world.level.block.entity.SignBlockEntity;
import net.minecraft.world.level.block.entity.SignText;
import net.minecraft.world.phys.Vec3;

public class SignBufferRenderer {

    private static final Cleaner cleaner = Cleaner.create();
    private static final Minecraft minecraft = Minecraft.getInstance();
    private static Model model = null;
    private RenderTarget frontTexture;
    private RenderTarget backtTexture;

    public SignBufferRenderer(SignBlockEntity arg, int light) {
        frontTexture = new TextureTarget((int) ExordiumModBase.signSettings.bufferWidth,
                (int) ExordiumModBase.signSettings.bufferHeight, false, false);
        frontTexture.setClearColor(0, 0, 0, 0);
        frontTexture.clear(false);
        cleaner.register(this, new State(frontTexture));
        backtTexture = new TextureTarget((int) ExordiumModBase.signSettings.bufferWidth,
                (int) ExordiumModBase.signSettings.bufferHeight, false, false);
        backtTexture.setClearColor(0, 0, 0, 0);
        backtTexture.clear(false);
        cleaner.register(this, new State(backtTexture));
        if (model == null)
            initializeModel();
        // restore renderlogic
        Minecraft.getInstance().getMainRenderTarget().bindWrite(true);
    }

    public void refreshImage(SignBlockEntity arg, int light, boolean front) {
        ExordiumModBase.instance.getDelayedRenderCallManager().addRenderCall(() -> {
            if (front) {
                frontTexture.bindWrite(false);
                frontTexture.clear(false);
                renderSignToBuffer(arg.getFrontText(), frontTexture, light);
            } else {
                backtTexture.bindWrite(false);
                backtTexture.clear(false);
                renderSignToBuffer(arg.getBackText(), backtTexture, light);
            }
        });
    }

    private static void initializeModel() {
        float height = (int) ExordiumModBase.signSettings.renderHeight;
        float width = (int) ExordiumModBase.signSettings.renderWidth;

        Vector3f[] modelData = new Vector3f[] {
                new Vector3f(0.0f, height, 0.01F),
                new Vector3f(width, height, 0.01F),
                new Vector3f(width, 0.0f, 0.01F),
                new Vector3f(0.0f, 0.0f, 0.01F),
        };
        Vector2f[] uvData = new Vector2f[] {
                new Vector2f(0.0f, 0.0f),
                new Vector2f(1.0f, 0.0f),
                new Vector2f(1.0f, 1.0f),
                new Vector2f(0.0f, 1.0f),
        };
        model = new Model(modelData, uvData);
    }

    Vec3 getTextOffset(float f) {
        return new Vec3(0.0D, (0.5F * f), (0.07F * f));
    }

    public void render(PoseStack poseStack, int light, boolean hangingSign, boolean front) {
//        poseStack.pushPose();
//        float scale = hangingSign ? 1.0F : 0.6666667F;
//        float g = 0.015625F * scale;
//        Vec3 vec3 = getTextOffset(scale);
//        poseStack.translate(vec3.x, vec3.y, vec3.z);
//        poseStack.scale(g, -g, g);
        poseStack.translate(ExordiumModBase.signSettings.offsetX,
                ExordiumModBase.signSettings.offsetY + (hangingSign ? ExordiumModBase.signSettings.hangingOffsetY : 0),
                hangingSign ? ExordiumModBase.signSettings.hangingOffsetZ : 0);
        RenderSystem.depthMask(true);
        RenderSystem.enableDepthTest();
        RenderSystem.enableBlend();
        RenderSystem.blendFunc(GlStateManager.SourceFactor.ONE, GlStateManager.DestFactor.ONE_MINUS_SRC_ALPHA);
        RenderSystem.setShader(GameRenderer::getPositionTexShader);
        RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, 1.0F);
        RenderSystem.setShaderTexture(0, front ? frontTexture.getColorTextureId() : backtTexture.getColorTextureId());
        Matrix4f pose = poseStack.last().pose();

        model.draw(pose); // TODO: is light required here, since it's baked into the texture?

//        poseStack.popPose();
    }

    private void renderSignToBuffer(SignText text, RenderTarget texture, int light) {
        MultiBufferSource.BufferSource bufferSource = MultiBufferSource
                .immediate(Tesselator.getInstance().getBuilder());
        texture.bindWrite(false);
        // cache the current render state
        Matrix4f tmp = RenderSystem.getProjectionMatrix();
        Matrix3f tmpI = RenderSystem.getInverseViewRotationMatrix();
        // set the renderstate to identity matrices
//        RenderSystem.disableCull();
        RenderSystem.setInverseViewRotationMatrix(new Matrix3f());
        RenderSystem.setProjectionMatrix(new Matrix4f(), RenderSystem.getVertexSorting());
        float scale = 1 / ExordiumModBase.signSettings.scaleSize;
        // matrix used for the text
        Matrix4f matrix4f = new Matrix4f().m00(scale).m11(-scale).m22(scale).m33(1);// Matrix4f.createScaleMatrix(scale,
                                                                                    // -scale, scale);
        int n;
        boolean bl;
        int o;
        int l = getDarkColor(text);
        FormattedCharSequence[] formattedCharSequences = text
                .getRenderMessages(Minecraft.getInstance().isTextFilteringEnabled(), arg2 -> {
                    List<FormattedCharSequence> list = minecraft.font.split(arg2, 90);
                    return list.isEmpty() ? FormattedCharSequence.EMPTY : list.get(0);
                });
        if (text.hasGlowingText()) {
            n = text.getColor().getTextColor();
            bl = true;// isOutlineVisible(arg, n);
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
                minecraft.font.drawInBatch8xOutline(formattedCharSequence, -28 + q, (p * 10 - 20), n, l, matrix4f,
                        bufferSource,
                        o);
            } else {
                minecraft.font.drawInBatch(formattedCharSequence, (-28 + q), (p * 10 - 20), n, false, matrix4f,
                        bufferSource,
                        Font.DisplayMode.NORMAL, 0, o);
            }
        }
        bufferSource.endBatch(); // force clear the vertex consumer
        // restore renderlogic
        Minecraft.getInstance().getMainRenderTarget().bindWrite(true);
        RenderSystem.setProjectionMatrix(tmp, RenderSystem.getVertexSorting());
        RenderSystem.setInverseViewRotationMatrix(tmpI);
    }

    private static int getDarkColor(SignText text) {
        int i = text.getColor().getTextColor();
        if (i == DyeColor.BLACK.getTextColor() && text.hasGlowingText())
            return -988212;
        int j = (int) (FastColor.ARGB32.red(i) * 0.4D);
        int k = (int) (FastColor.ARGB32.green(i) * 0.4D);
        int l = (int) (FastColor.ARGB32.blue(i) * 0.4D);
        return FastColor.ARGB32.color(0, j, k, l);
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
