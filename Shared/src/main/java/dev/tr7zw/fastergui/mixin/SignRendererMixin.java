package dev.tr7zw.fastergui.mixin;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.At.Shift;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import com.mojang.blaze3d.vertex.PoseStack;

import dev.tr7zw.fastergui.access.SignBufferHolder;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.blockentity.SignRenderer;
import net.minecraft.world.level.block.entity.SignBlockEntity;

@Mixin(SignRenderer.class)
public class SignRendererMixin {

    @Inject(method = "render", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/renderer/blockentity/SignRenderer;getDarkColor(Lnet/minecraft/world/level/block/entity/SignBlockEntity;)I", shift = Shift.BEFORE), cancellable = true)
    public void render(SignBlockEntity signBlockEntity, float f, PoseStack poseStack,
            MultiBufferSource multiBufferSource, int i, int j, CallbackInfo info) {
        boolean cancel = ((SignBufferHolder)signBlockEntity).renderBuffered(poseStack, multiBufferSource);
        if(cancel) {
            poseStack.popPose();
            info.cancel();
        }
    }
    
}
