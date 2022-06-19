package dev.tr7zw.fastergui.mixin;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import com.mojang.blaze3d.platform.GlStateManager;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;

import dev.tr7zw.fastergui.FasterGuiModBase;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.block.model.ItemTransforms;
import net.minecraft.client.renderer.entity.ItemRenderer;
import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.world.item.ItemStack;

@Mixin(ItemRenderer.class)
public class ItemRendererMixin {

    @Inject(method = "renderGuiItem(Lnet/minecraft/world/item/ItemStack;IILnet/minecraft/client/resources/model/BakedModel;)V", at = @At("HEAD"))
    protected void renderGuiItem(ItemStack itemStack, int i, int j, BakedModel bakedModel, CallbackInfo ci) {
        FasterGuiModBase.setBlendBypass(true);
    }
    
    @Inject(method = "renderGuiItem(Lnet/minecraft/world/item/ItemStack;IILnet/minecraft/client/resources/model/BakedModel;)V", at = @At("RETURN"))
    protected void renderGuiItemReturn(ItemStack itemStack, int i, int j, BakedModel bakedModel, CallbackInfo ci) {
        FasterGuiModBase.setBlendBypass(false);
    }
    
    //Workaround for REI, because REI renders Gui items not as Gui Items...
    
    @Inject(method = "render", at = @At("HEAD"))
    public void render(ItemStack itemStack, ItemTransforms.TransformType transformType, boolean bl, PoseStack poseStack,
            MultiBufferSource multiBufferSource, int i, int j, BakedModel bakedModel, CallbackInfo ci) {
        if(FasterGuiModBase.isForceBlend() && (!FasterGuiModBase.isBlendBypass() || FasterGuiModBase.getBypassTurnoff() > 0)) {
            FasterGuiModBase.setBlendBypass(true);
            RenderSystem.enableBlend();
            RenderSystem.blendFunc(GlStateManager.SourceFactor.SRC_ALPHA, GlStateManager.DestFactor.ONE_MINUS_SRC_ALPHA);
            FasterGuiModBase.setBypassTurnoff(4);
        }
    }
    
//    @Inject(method = "render", at = @At("RETURN"))
//    public void renderReturn(ItemStack itemStack, ItemTransforms.TransformType transformType, boolean bl, PoseStack poseStack,
//            MultiBufferSource multiBufferSource, int i, int j, BakedModel bakedModel, CallbackInfo ci) {
//        FasterGuiModBase.setBlendBypass(false);
//    }
    
}
