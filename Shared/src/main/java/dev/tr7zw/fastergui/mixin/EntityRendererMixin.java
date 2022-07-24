package dev.tr7zw.fastergui.mixin;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Matrix4f;

import dev.tr7zw.fastergui.FasterGuiModBase;
import dev.tr7zw.fastergui.access.NametagBufferHolder;
import dev.tr7zw.fastergui.util.NametagScreenBuffer;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
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
    
    /**
     * Hooking into the render method to render the nametag. Redirects to the {@link NametagBufferHolder} class.
     * 
     * @author tr7zw
     * @reason render nametags
     */
    @SuppressWarnings("resource")
    @Inject(method = "renderNameTag", at = @At("HEAD"), cancellable = true)
    protected void renderNameTag(Entity entity, Component component, PoseStack poseStack, MultiBufferSource mbs, int i, CallbackInfo ci) {
        NametagScreenBuffer buffer = null;
        FasterGuiModBase inst = FasterGuiModBase.instance;
        if(inst.config.enableNametagScreenBuffering) {
            buffer = inst.getNameTagScreenBuffer();
            mbs.getBuffer(RenderType.endGateway()); // force clear the vertex consumer
            if(!entity.isDiscrete() && !buffer.bind()) {
                ci.cancel(); // the buffer is not ready, so the last frame will be used instead
                return;
            }
            if(!inst.config.enableNametagBuffering) {
                // partial copy of the method to remove the "behind walls" part
                float f = entity.getBbHeight() + 0.5F;
                poseStack.pushPose();
                poseStack.translate(0.0D, f, 0.0D);
                poseStack.mulPose(this.entityRenderDispatcher.cameraOrientation());
                poseStack.scale(-0.025F, -0.025F, 0.025F);
                Matrix4f matrix4f = poseStack.last().pose();
                float h = (-font.width(component) / 2);
                float g = (Minecraft.getInstance()).options.getBackgroundOpacity(0.25F);
                int k = (int) (g * 255.0F) << 24;
                font.drawInBatch(component, h, 0, -1, false, matrix4f, mbs, true, k, i);
                poseStack.popPose();
                mbs.getBuffer(RenderType.endGateway()); // force clear the vertex consumer
                buffer.bindEnd();
                ci.cancel();
                return;
            }
        }
        if(inst.config.enableNametagBuffering) {
            boolean sneaking = entity.isDiscrete();
            float f = entity.getBbHeight() + 0.5F;
            poseStack.pushPose();
            poseStack.translate(0.0D, f, 0.0D);
            poseStack.mulPose(this.entityRenderDispatcher.cameraOrientation());
            poseStack.scale(-0.025F, -0.025F, 0.025F);
            boolean cancel = ((NametagBufferHolder)entity).renderBuffered(component, poseStack, mbs, i, sneaking);
            poseStack.popPose();
            if(cancel) {
                ci.cancel();
                if(buffer != null)
                    buffer.bindEnd();
            }
        }
    }
    
    @Inject(method = "renderNameTag", at = @At("TAIL"), cancellable = true)
    protected void renderNameTagEnd(Entity arg, Component arg2, PoseStack poseStack, MultiBufferSource mbs, int k, CallbackInfo ci) {
        if(FasterGuiModBase.instance.config.enableNametagScreenBuffering) {
            mbs.getBuffer(RenderType.endGateway()); // force clear the vertex consumer
            NametagScreenBuffer buffer = FasterGuiModBase.instance.getNameTagScreenBuffer();
            buffer.bindEnd();
        }
    }
    
}
