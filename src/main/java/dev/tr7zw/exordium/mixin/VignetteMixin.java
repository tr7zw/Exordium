package dev.tr7zw.exordium.mixin;

import net.minecraft.client.gui.screens.Screen;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import com.mojang.blaze3d.systems.RenderSystem;

import dev.tr7zw.exordium.ExordiumModBase;
import dev.tr7zw.exordium.access.VanillaBufferAccess.VignetteOverlayAccess;
import dev.tr7zw.exordium.util.BufferedComponent;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.border.WorldBorder;

@Mixin(Gui.class)
public class VignetteMixin implements VignetteOverlayAccess {
    private static ResourceLocation FAST_VIGNETTE_LOCATION = new ResourceLocation("exordium",
            "textures/misc/fast_vignette.png");
    @Unique
    private float exordium_state = 0f;
    @Unique
    private float exordium_lastVignetteBrightness = 1.0F;

    @Unique
    private float exordium_getVignetteBrightness() {
        return ((Gui) (Object) this).vignetteBrightness;
    }

    @Unique
    private BufferedComponent exordium_vignetteBuffer = new BufferedComponent(false,
            () -> ExordiumModBase.instance.config.vignetteSettings) {

        @Override
        public boolean shouldRenderNextCappedFrame() {
            if (exordium_lastVignetteBrightness != exordium_getVignetteBrightness()) {
                return true;
            }
            WorldBorder worldBorder = Minecraft.getInstance().level.getWorldBorder();
            float f = (float) worldBorder.getDistanceToBorder(Minecraft.getInstance().getCameraEntity());
            double d = Math.min(worldBorder.getLerpSpeed() * (double) worldBorder.getWarningTime() * 1000.0D,
                    Math.abs(worldBorder.getLerpTarget() - worldBorder.getSize()));
            double e = Math.max((double) worldBorder.getWarningBlocks(), d);
            if ((double) f < e) {
                f = 1.0F - (float) ((double) f / e);
            } else {
                f = 0.0F;
            }
            return exordium_state == f;
        }

        @Override
        public void captureState() {
            WorldBorder worldBorder = Minecraft.getInstance().level.getWorldBorder();
            float f = (float) worldBorder.getDistanceToBorder(Minecraft.getInstance().getCameraEntity());
            double d = Math.min(worldBorder.getLerpSpeed() * (double) worldBorder.getWarningTime() * 1000.0D,
                    Math.abs(worldBorder.getLerpTarget() - worldBorder.getSize()));
            double e = Math.max((double) worldBorder.getWarningBlocks(), d);
            if ((double) f < e) {
                f = 1.0F - (float) ((double) f / e);
            } else {
                f = 0.0F;
            }
            exordium_state = f;
            exordium_lastVignetteBrightness = exordium_getVignetteBrightness();
        }
    };

    @WrapOperation(method = "renderCameraOverlays", at = {
            @At(value = "INVOKE", target = "Lnet/minecraft/client/gui/Gui;renderVignette(Lnet/minecraft/client/gui/GuiGraphics;Lnet/minecraft/world/entity/Entity;)V"), })
    private void renderVignetteWrapper(Gui gui, GuiGraphics guiGraphics, Entity entity,
            final Operation<Void> operation) {
        if (!exordium_vignetteBuffer.render()) {
            if (ExordiumModBase.instance.config.vignetteSettings.enabled) {
                renderCustomVignette(guiGraphics);
            } else {
                operation.call(gui, guiGraphics, entity);
            }
        }
        exordium_vignetteBuffer.renderEnd();
    }

    public void renderCustomVignette(GuiGraphics guiGraphics) {
        RenderSystem.disableDepthTest();
        RenderSystem.depthMask(false);
//            RenderSystem.blendFuncSeparate(SourceFactor.ZERO, DestFactor.ONE_MINUS_SRC_COLOR, SourceFactor.ONE,
//                    DestFactor.ZERO);
        ExordiumModBase.correctBlendMode();
        float f = exordium_state;
        if (f > 0.0F) {
            f = Mth.clamp(f, 0.0F, 1.0F);
            guiGraphics.setColor(f, 0.0F, 0.0F, 1.0F);
        } else {
            float g = ((Gui) (Object) this).vignetteBrightness;
            g = Mth.clamp(g, 0.0F, 1.0F);
            guiGraphics.setColor(0, 0, 0, ((Gui) (Object) this).vignetteBrightness);
        }

        Screen screen = Minecraft.getInstance().screen;

        if (screen != null) {
            guiGraphics.blit(FAST_VIGNETTE_LOCATION, 0, 0, -90, 0.0F, 0.0F, screen.width, screen.height, screen.width,
                    screen.height);
        }

        RenderSystem.depthMask(true);
        RenderSystem.enableDepthTest();
        guiGraphics.setColor(1.0F, 1.0F, 1.0F, 1.0F);
        RenderSystem.defaultBlendFunc();
    }

    @Override
    public BufferedComponent getVignetteOverlayBuffer() {
        return exordium_vignetteBuffer;
    }

}
