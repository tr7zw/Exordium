package dev.tr7zw.exordium.mixin;

import org.joml.Matrix4f;
import org.joml.Vector3f;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.Tesselator;

import dev.tr7zw.exordium.ExordiumModBase;
import dev.tr7zw.exordium.util.NametagScreenBuffer;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.entity.EntityRenderDispatcher;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.Entity;

@Mixin(value = EntityRenderer.class)
public class EntityRendererMixin {

    @Shadow
    protected EntityRenderDispatcher entityRenderDispatcher;
    @Shadow
    private Font font;

    @Inject(method = "renderNameTag", at = @At("HEAD"), cancellable = true)
    protected void renderNameTag(Entity entity, Component component, PoseStack tmpPoseStack,
            MultiBufferSource unusedBuffer, int i, float f2, CallbackInfo ci) {
        NametagScreenBuffer buffer = null;
        ExordiumModBase inst = ExordiumModBase.instance;
        if (inst.config.enableNametagScreenBuffering) {
            buffer = inst.getNameTagScreenBuffer();
            if (!entity.isDiscrete() && !buffer.acceptsData()) {
                ci.cancel(); // the buffer is not ready, so the last frame will be used instead
                return;
            }
            Matrix4f matrix4f = new Matrix4f(tmpPoseStack.last().pose());
            float f = entity.getBbHeight() + 0.5F;
            matrix4f.translate(new Vector3f(0.0f, f, 0.0f));
            matrix4f.rotate(this.entityRenderDispatcher.cameraOrientation());
            matrix4f.scale(-0.025F, -0.025F, 0.025F);
            ExordiumModBase.instance.getDelayedRenderCallManager().addNametagRenderCall(() -> {
                // partial copy of the method to remove the "behind walls" part
                MultiBufferSource.BufferSource bufferSource = MultiBufferSource
                        .immediate(Tesselator.getInstance().getBuilder());
                float h = (-font.width(component) / 2);
                float g = (Minecraft.getInstance()).options.getBackgroundOpacity(0.25F);
                int k = (int) (g * 255.0F) << 24;
                font.drawInBatch(component, h, 0, -1, false, matrix4f, bufferSource, Font.DisplayMode.NORMAL, k, i);
                bufferSource.endBatch();

            });
            ci.cancel();
        }
    }
}
