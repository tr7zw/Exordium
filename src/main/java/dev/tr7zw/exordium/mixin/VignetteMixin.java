package dev.tr7zw.exordium.mixin;

//#if MC >= 12102
import net.minecraft.client.renderer.RenderType;
import net.minecraft.util.ARGB;
//#endif
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import com.mojang.blaze3d.systems.RenderSystem;

import dev.tr7zw.exordium.ExordiumModBase;
import dev.tr7zw.exordium.components.BufferInstance;
import dev.tr7zw.exordium.components.vanilla.VignetteComponent;
import dev.tr7zw.util.NMSHelper;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.border.WorldBorder;

@Mixin(Gui.class)
public class VignetteMixin {

    @Shadow
    @Final
    private Minecraft minecraft;

    private static ResourceLocation FAST_VIGNETTE_LOCATION = NMSHelper.getResourceLocation("exordium",
            "textures/misc/fast_vignette.png");
    private static ResourceLocation FAST_VIGNETTE_DARK_LOCATION = NMSHelper.getResourceLocation("exordium",
            "textures/misc/fast_vignette_dark.png");

    @WrapOperation(method = "renderCameraOverlays", at = {
            @At(value = "INVOKE", target = "Lnet/minecraft/client/gui/Gui;renderVignette(Lnet/minecraft/client/gui/GuiGraphics;Lnet/minecraft/world/entity/Entity;)V"), })
    private void renderVignetteWrapper(Gui gui, GuiGraphics guiGraphics, Entity entity,
            final Operation<Void> operation) {
        BufferInstance<Float> buffer = ExordiumModBase.instance.getBufferManager()
                .getBufferInstance(VignetteComponent.getId(), Float.class);
        float brightness = ((Gui) (Object) this).vignetteBrightness;
        if (!buffer.renderBuffer(0, brightness, guiGraphics)) {
            if (buffer.enabled()) {
                renderCustomVignette(guiGraphics);
            } else {
                operation.call(gui, guiGraphics, entity);
            }
        }
        buffer.postRender(brightness, guiGraphics);
    }

    public void renderCustomVignette(GuiGraphics guiGraphics) {
        // FIXME: breaks the 1.21 pause menu blur
        WorldBorder worldBorder = minecraft.level.getWorldBorder();
        float f = 0.0F;
        float g = (float) worldBorder.getDistanceToBorder(minecraft.player);
        double d = Math.min(worldBorder.getLerpSpeed() * (double) worldBorder.getWarningTime() * 1000.0,
                Math.abs(worldBorder.getLerpTarget() - worldBorder.getSize()));
        double e = Math.max((double) worldBorder.getWarningBlocks(), d);
        if ((double) g < e) {
            f = 1.0F - (float) ((double) g / e);
        }
        RenderSystem.disableDepthTest();
        RenderSystem.depthMask(false);
        // RenderSystem.blendFuncSeparate(SourceFactor.ZERO,
        // DestFactor.ONE_MINUS_SRC_COLOR, SourceFactor.ONE,
        // DestFactor.ZERO);
        ExordiumModBase.correctBlendMode();
        ResourceLocation texture = FAST_VIGNETTE_DARK_LOCATION;
        float brightness = ((Gui) (Object) this).vignetteBrightness;
        g = Mth.clamp(brightness, 0.0F, 1.0F);
        int color = -1;
        if (f > 0.0F) {
            // red tint
            f = Mth.clamp(f, 0.0F, 1.0F);
            g = Math.max(g, f);
            //#if MC >= 12102
            color = ARGB.colorFromFloat(f, 0f, 0f, g);
            //#else
            //$$guiGraphics.setColor(f, 0.0F, 0.0F, g);
            //#endif
            texture = FAST_VIGNETTE_LOCATION;
        } else {
            //#if MC >= 12102
            color = ARGB.colorFromFloat(0f, 0f, 0f, g);
            //#else
            //$$guiGraphics.setColor(1.0F, 1.0F, 1.0F, g);
            //#endif
        }

        //#if MC >= 12102
        guiGraphics.blit(RenderType::guiTextured, texture, 0, 0, 0.0F, 0.0F, guiGraphics.guiWidth(),
                guiGraphics.guiHeight(), guiGraphics.guiWidth(), guiGraphics.guiHeight(), color);
        //#else
        //$$guiGraphics.blit(texture, 0, 0, -90, 0.0F, 0.0F, guiGraphics.guiWidth(), guiGraphics.guiHeight(),
        //$$                guiGraphics.guiWidth(), guiGraphics.guiHeight());
        //#endif

        RenderSystem.depthMask(true);
        RenderSystem.enableDepthTest();
        //#if MC < 12102
        //$$guiGraphics.setColor(1.0F, 1.0F, 1.0F, 1.0F);
        //#endif
        RenderSystem.defaultBlendFunc();
    }

}
