package dev.tr7zw.fastergui.mixin;

import java.text.DecimalFormat;
import java.util.Objects;

import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.BufferBuilder;
import com.mojang.blaze3d.vertex.BufferUploader;
import com.mojang.blaze3d.vertex.DefaultVertexFormat;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.Tesselator;
import com.mojang.blaze3d.vertex.VertexFormat;
import com.mojang.math.Matrix4f;
import com.mojang.math.Transformation;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiComponent;
import net.minecraft.client.gui.components.DebugScreenOverlay;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.util.FrameTimer;
import net.minecraft.util.Mth;

@Mixin(DebugScreenOverlay.class)
public abstract class DebugScreenOverlayMixin extends GuiComponent {

    @Shadow
    @Final
    private Minecraft minecraft;
    @Shadow
    @Final
    private Font font;

    private DecimalFormat df = new DecimalFormat("0.00");

    @Inject(method = "drawChart", at = @At("HEAD"), cancellable = true)
    private void drawChart(PoseStack poseStack, FrameTimer frameTimer, int i, int j, boolean fpsGraph,
            CallbackInfo ci) {
        if (!fpsGraph) {
            return;
        }
        RenderSystem.disableDepthTest();

        int k = frameTimer.getLogStart();
        int l = frameTimer.getLogEnd();
        long[] ls = frameTimer.getLog();
        int m = k;
        int n = i;
        int o = Math.max(0, ls.length - j);
        int p = ls.length - o;

        m = frameTimer.wrapIndex(m + o);

        long q = 0L;
        int r = Integer.MAX_VALUE;
        int s = Integer.MIN_VALUE;
        int t;
        for (t = 0; t < p; t++) {
            int u = (int) (ls[frameTimer.wrapIndex(m + t)] / 1000000L);
            r = Math.min(r, u);
            s = Math.max(s, u);
            q += u;

        }
        t = this.minecraft.getWindow().getGuiScaledHeight();
        fill(poseStack, i, t - 60, i + p, t, -1873784752);

        RenderSystem.setShader(GameRenderer::getPositionColorShader);

        BufferBuilder bufferBuilder = Tesselator.getInstance().getBuilder();
        RenderSystem.enableBlend();
        RenderSystem.disableTexture();
        RenderSystem.defaultBlendFunc();
        bufferBuilder.begin(VertexFormat.Mode.QUADS, DefaultVertexFormat.POSITION_COLOR);

        Matrix4f matrix4f = Transformation.identity().getMatrix();

        while (m != l) {
            int v = frameTimer.scaleSampleTo(ls[m], 60, 120); // 30 60
            int w = 200; // 100
            int x = getSampleColor(Mth.clamp(v, 0, w), 0, w / 2, w);

            int y = x >> 24 & 0xFF;
            int z = x >> 16 & 0xFF;
            int aa = x >> 8 & 0xFF;
            int ab = x & 0xFF;

            bufferBuilder.vertex(matrix4f, (n + 1), t, 0.0F).color(z, aa, ab, y).endVertex();
            bufferBuilder.vertex(matrix4f, (n + 1), (t - v + 1), 0.0F).color(z, aa, ab, y).endVertex();
            bufferBuilder.vertex(matrix4f, n, (t - v + 1), 0.0F).color(z, aa, ab, y).endVertex();
            bufferBuilder.vertex(matrix4f, n, t, 0.0F).color(z, aa, ab, y).endVertex();

            n++;
            m = frameTimer.wrapIndex(m + 1);

        }
        bufferBuilder.end();
        BufferUploader.end(bufferBuilder);
        RenderSystem.enableTexture();
        RenderSystem.disableBlend();
        // TODO fix
//        if (fpsGraph) {
//            fill(poseStack, i + 1, t - 30 + 1, i + 14, t - 30 + 10, -1873784752);
//            this.font.draw(poseStack, "60 FPS", (i + 2), (t - 30 + 2), 14737632);
//            hLine(poseStack, i, i + p - 1, t - 30, -1);
//
//            fill(poseStack, i + 1, t - 60 + 1, i + 14, t - 60 + 10, -1873784752);
//            this.font.draw(poseStack, "30 FPS", (i + 2), (t - 60 + 2), 14737632);
//            hLine(poseStack, i, i + p - 1, t - 60, -1);
//        } else {
//            fill(poseStack, i + 1, t - 60 + 1, i + 14, t - 60 + 10, -1873784752);
//            this.font.draw(poseStack, "20 TPS", (i + 2), (t - 60 + 2), 14737632);
//            hLine(poseStack, i, i + p - 1, t - 60, -1);
//
//        }
        hLine(poseStack, i, i + p - 1, t - 1, -1);
        vLine(poseStack, i, t - 60, t, -1);
        vLine(poseStack, i + p - 1, t - 60, t, -1);

        if (fpsGraph && this.minecraft.options.framerateLimit > 0 && this.minecraft.options.framerateLimit <= 250)
            hLine(poseStack, i, i + p - 1, t - 1 - (int) (1800.0D / this.minecraft.options.framerateLimit), -16711681);

        String string = "" + r + " ms min";
        String string2 = "" + df.format((float) q / p) + " ms avg";
        String string3 = "" + s + " ms max";
        Objects.requireNonNull(this.font);
        this.font.drawShadow(poseStack, string, (i + 2), (t - 60 - 9), 14737632);
        Objects.requireNonNull(this.font);
        this.font.drawShadow(poseStack, string2, (i + p / 2 - this.font.width(string2) / 2), (t - 60 - 9), 14737632);
        Objects.requireNonNull(this.font);
        this.font.drawShadow(poseStack, string3, (i + p - this.font.width(string3)), (t - 60 - 9), 14737632);

        RenderSystem.enableDepthTest();
        ci.cancel();
    }

    @Shadow
    private int getSampleColor(int i, int j, int k, int l) {
        return 0;
    }

}
