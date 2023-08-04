package dev.tr7zw.exordium.mixin;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import com.mojang.blaze3d.vertex.PoseStack;

import dev.tr7zw.exordium.ExordiumModBase;
import dev.tr7zw.exordium.access.SignBufferHolder;
import net.minecraft.client.model.Model;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.blockentity.SignRenderer;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.SignBlock;
import net.minecraft.world.level.block.entity.SignBlockEntity;
import net.minecraft.world.level.block.entity.SignText;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.WoodType;
import net.minecraft.world.phys.Vec3;

@Mixin(SignRenderer.class)
public abstract class SignRendererMixin {

    private SignBlockEntity renderingSign;
    
    @Inject(method = "renderSignWithText", at = @At("HEAD"))
    void renderSignWithText(SignBlockEntity signEntity, PoseStack poseStack, MultiBufferSource buffer, int packedLight,
            int packedOverlay, BlockState state, SignBlock signBlock, WoodType woodType, Model model, CallbackInfo info) {
        this.renderingSign = signEntity;
    }
    
    @Inject(method = "renderSignText", at = @At("HEAD"), cancellable = true)
    public void render(BlockPos pos, SignText text, PoseStack poseStack, MultiBufferSource buffer, int packedLight,
            int lineHeight, int maxWidth, boolean isFrontText, CallbackInfo info) {
        if (!ExordiumModBase.instance.config.enableSignBuffering || renderingSign == null) {
            return;
        }
        poseStack.pushPose();
        translateSignText(poseStack, isFrontText, getTextOffset());
        boolean cancel = ((SignBufferHolder) renderingSign).renderBuffered(poseStack, buffer, isFrontText, packedLight);
        poseStack.popPose();
        if (cancel) {
            info.cancel();
        }
    }
    
    @Shadow
    abstract Vec3 getTextOffset();
    @Shadow
    abstract void translateSignText(PoseStack poseStack, boolean isFrontText, Vec3 offset);

}
