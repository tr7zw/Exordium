package dev.tr7zw.exordium.mixin;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import com.mojang.blaze3d.vertex.PoseStack;

import dev.tr7zw.exordium.ExordiumModBase;
import dev.tr7zw.exordium.access.SignBufferHolder;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.blockentity.SignRenderer;
import net.minecraft.world.level.block.entity.SignBlockEntity;

@Mixin(SignRenderer.class)
public class SignRendererMixin {

    @Inject(method = "renderSignText", at = @At("HEAD"), cancellable = true)
    public void render(SignBlockEntity signBlockEntity, PoseStack poseStack, MultiBufferSource multiBufferSource, int light, float j, CallbackInfo info) {
        if (!ExordiumModBase.instance.config.enableSignBuffering) {
            return;
        }
        boolean cancel = ((SignBufferHolder) signBlockEntity).renderBuffered(poseStack, multiBufferSource, light);
        if (cancel) {
            poseStack.popPose();
            info.cancel();
        }
    }

}
