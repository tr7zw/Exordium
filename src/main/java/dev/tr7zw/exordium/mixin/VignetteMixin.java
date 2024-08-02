package dev.tr7zw.exordium.mixin;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import com.mojang.blaze3d.systems.RenderSystem;

import dev.tr7zw.exordium.ExordiumModBase;
import dev.tr7zw.exordium.components.BufferInstance;
import dev.tr7zw.exordium.components.vanilla.VignetteComponent;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.Entity;

@Mixin(Gui.class)
public class VignetteMixin {

    private static ResourceLocation FAST_VIGNETTE_LOCATION = new ResourceLocation("exordium",
            "textures/misc/fast_vignette.png");

    @WrapOperation(method = "renderCameraOverlays", at = {
            @At(value = "INVOKE", target = "Lnet/minecraft/client/gui/Gui;renderVignette(Lnet/minecraft/client/gui/GuiGraphics;Lnet/minecraft/world/entity/Entity;)V"), })
    private void renderVignetteWrapper(Gui gui, GuiGraphics guiGraphics, Entity entity,
            final Operation<Void> operation) {
        BufferInstance<Float> buffer = ExordiumModBase.instance.getBufferManager()
                .getBufferInstance(VignetteComponent.getId(), Float.class);
        float brightness = ((Gui) (Object) this).vignetteBrightness;
        if (!buffer.renderBuffer(0, brightness)) {
            if (ExordiumModBase.instance.config.vignetteSettings.isEnabled()) {
                renderCustomVignette(guiGraphics, brightness);
            } else {
                operation.call(gui, guiGraphics, entity);
            }
        }
        buffer.postRender(brightness);
    }

    public void renderCustomVignette(GuiGraphics guiGraphics, float brightness) {
        RenderSystem.disableDepthTest();
        RenderSystem.depthMask(false);
        // RenderSystem.blendFuncSeparate(SourceFactor.ZERO,
        // DestFactor.ONE_MINUS_SRC_COLOR, SourceFactor.ONE,
        // DestFactor.ZERO);
        ExordiumModBase.correctBlendMode();
        float f = brightness;
        if (f > 0.0F) {
            f = Mth.clamp(f, 0.0F, 1.0F);
            guiGraphics.setColor(f, 0.0F, 0.0F, 1.0F);
        } else {
            float g = ((Gui) (Object) this).vignetteBrightness;
            g = Mth.clamp(g, 0.0F, 1.0F);
            guiGraphics.setColor(0, 0, 0, ((Gui) (Object) this).vignetteBrightness);
        }

        Screen screen = Minecraft.getInstance().screen;

        if (screen == null) {
            guiGraphics.blit(FAST_VIGNETTE_LOCATION, 0, 0, -90, 0.0F, 0.0F, screen.width, screen.height, screen.width,
                    screen.height);
        }

        RenderSystem.depthMask(true);
        RenderSystem.enableDepthTest();
        guiGraphics.setColor(1.0F, 1.0F, 1.0F, 1.0F);
        RenderSystem.defaultBlendFunc();
    }

}
