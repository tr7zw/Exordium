package dev.tr7zw.fastergui.mixin;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import com.mojang.blaze3d.vertex.PoseStack;

import dev.tr7zw.fastergui.FasterGuiModBase;
import dev.tr7zw.fastergui.access.NametagBufferHolder;
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
    
    /**
     * Hooking into the render method to render the nametag. Redirects to the {@link NametagBufferHolder} class.
     * 
     * @author tr7zw
     * @reason render nametags
     */
    @Inject(method = "renderNameTag", at = @At("HEAD"), cancellable = true)
    protected void renderNameTag(Entity arg, Component arg2, PoseStack poseStack, MultiBufferSource arg4, int k, CallbackInfo ci) {
        if(!FasterGuiModBase.instance.config.enableNametagBuffering) {
            return;
        }
        boolean flag = !arg.isDiscrete();
        float f = arg.getBbHeight() + 0.5F;
        poseStack.pushPose();
        poseStack.translate(0.0D, f, 0.0D);
        poseStack.mulPose(this.entityRenderDispatcher.cameraOrientation());
        poseStack.scale(-0.025F, -0.025F, 0.025F);
        boolean cancel = ((NametagBufferHolder)arg).renderBuffered(arg2, poseStack, arg4, k, flag);
        poseStack.popPose();
        if(cancel)
            ci.cancel();
    }
    
}
