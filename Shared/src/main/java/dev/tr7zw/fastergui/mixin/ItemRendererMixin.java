package dev.tr7zw.fastergui.mixin;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import dev.tr7zw.fastergui.FasterGuiModBase;
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
    
}
