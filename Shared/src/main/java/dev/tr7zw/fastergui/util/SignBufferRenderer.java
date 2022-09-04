package dev.tr7zw.fastergui.util;

import java.lang.ref.Cleaner;
import java.util.List;
import com.mojang.blaze3d.pipeline.RenderTarget;
import com.mojang.blaze3d.pipeline.TextureTarget;
import com.mojang.blaze3d.platform.GlStateManager;
import com.mojang.blaze3d.platform.NativeImage;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Matrix3f;
import com.mojang.math.Matrix4f;
import com.mojang.math.Vector3f;

import dev.tr7zw.fastergui.FasterGuiModBase;
import dev.tr7zw.fastergui.util.Model.Vector2f;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.util.FormattedCharSequence;
import net.minecraft.world.item.DyeColor;
import net.minecraft.world.level.block.entity.SignBlockEntity;

public class SignBufferRenderer {

    private static final Cleaner cleaner = Cleaner.create();
    private static final Minecraft minecraft = Minecraft.getInstance();
    private static Model model = null;
    private RenderTarget guiTarget;
    
    public SignBufferRenderer(SignBlockEntity arg, int light) {
        guiTarget = new TextureTarget((int)FasterGuiModBase.signSettings.bufferWidth, (int)FasterGuiModBase.signSettings.bufferHeight, false, false);
        guiTarget.setClearColor(0, 0, 0, 0);
        guiTarget.clear(false);
        cleaner.register(this, new State(guiTarget));
        if(model == null)
            initializeModel();
    }
    
    public void refreshImage(SignBlockEntity arg, int light) {
        FasterGuiModBase.instance.getDelayedRenderCallManager().addRenderCall(() -> {
            guiTarget.bindWrite(false);
            guiTarget.clear(false);
            renderSignToBuffer(arg, light);
        });
    }
    
    private static void initializeModel(){
        float height = (int)FasterGuiModBase.signSettings.renderHeight;
        float width = (int)FasterGuiModBase.signSettings.renderWidth;

        Vector3f[] modelData = new Vector3f[]{
            new Vector3f(0.0f, height, 0.01F),
            new Vector3f(width, height, 0.01F),
            new Vector3f(width, 0.0f, 0.01F),
            new Vector3f(0.0f, 0.0f, 0.01F),
        };
        Vector2f[] uvData = new Vector2f[]{
            new Vector2f(0.0f, 0.0f),
            new Vector2f(1.0f, 0.0f),
            new Vector2f(1.0f, 1.0f),
            new Vector2f(0.0f, 1.0f),
        };
        model = new Model(modelData, uvData);
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

        Matrix4f pose = poseStack.last().pose();
        
        model.draw(pose); // TODO: is light required here, since it's baked into the texture?

        poseStack.popPose();
    }
    
    private void renderSignToBuffer(SignBlockEntity arg, int light) {
        MultiBufferSource.BufferSource bufferSource = MultiBufferSource
                .immediate(Tesselator.getInstance().getBuilder());
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
                minecraft.font.drawInBatch8xOutline(formattedCharSequence,-28 +  q, (p * 10 - 20), n, l, matrix4f, bufferSource,
                        o);
            } else {
                minecraft.font.drawInBatch(formattedCharSequence, (-28 + q), (p * 10 - 20), n, false, matrix4f, bufferSource,
                        false, 0, o);
            }
        }   
        bufferSource.endBatch(); // force clear the vertex consumer
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
