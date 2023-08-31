package dev.tr7zw.exordium.mixin;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import com.mojang.blaze3d.systems.RenderSystem;

import dev.tr7zw.exordium.ExordiumModBase;
import dev.tr7zw.exordium.util.BufferedComponent;
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
    private Minecraft minecraft;
    @Shadow
    private int screenWidth;
    @Shadow
    private int screenHeight;
    private static ResourceLocation FAST_VIGNETTE_LOCATION = new ResourceLocation("exordium",
            "textures/misc/fast_vignette.png");
    @Shadow
    private float vignetteBrightness = 1.0F;
    private float state = 0f;
    private float lastVignetteBrightness = 1.0F;

    private BufferedComponent vignetteBuffer = new BufferedComponent(true,
            ExordiumModBase.instance.config.vignetteSettings) {

        @Override
        public boolean needsRender() {
            if (lastVignetteBrightness != vignetteBrightness) {
                return true;
            }
            WorldBorder worldBorder = minecraft.level.getWorldBorder();
            float f = (float) worldBorder.getDistanceToBorder(minecraft.getCameraEntity());
            double d = Math.min(worldBorder.getLerpSpeed() * (double) worldBorder.getWarningTime() * 1000.0D,
                    Math.abs(worldBorder.getLerpTarget() - worldBorder.getSize()));
            double e = Math.max((double) worldBorder.getWarningBlocks(), d);
            if ((double) f < e) {
                f = 1.0F - (float) ((double) f / e);
            } else {
                f = 0.0F;
            }
            return state == f;
        }

        @Override
        public void captureState() {
            WorldBorder worldBorder = minecraft.level.getWorldBorder();
            float f = (float) worldBorder.getDistanceToBorder(minecraft.getCameraEntity());
            double d = Math.min(worldBorder.getLerpSpeed() * (double) worldBorder.getWarningTime() * 1000.0D,
                    Math.abs(worldBorder.getLerpTarget() - worldBorder.getSize()));
            double e = Math.max((double) worldBorder.getWarningBlocks(), d);
            if ((double) f < e) {
                f = 1.0F - (float) ((double) f / e);
            } else {
                f = 0.0F;
            }
            state = f;
            lastVignetteBrightness = vignetteBrightness;
        }
    };

    @WrapOperation(method = "render", at = {
            @At(value = "INVOKE", target = "Lnet/minecraft/client/gui/Gui;renderVignette(Lnet/minecraft/client/gui/GuiGraphics;Lnet/minecraft/world/entity/Entity;)V"),
    })
    private void renderVignetteWrapper(Gui gui, GuiGraphics guiGraphics, Entity entity,
            final Operation<Void> operation) {
        if (!vignetteBuffer.render()) {
            if (ExordiumModBase.instance.config.vignetteSettings.enabled) {
                RenderSystem.disableDepthTest();
                RenderSystem.depthMask(false);
//            RenderSystem.blendFuncSeparate(SourceFactor.ZERO, DestFactor.ONE_MINUS_SRC_COLOR, SourceFactor.ONE,
//                    DestFactor.ZERO);
                ExordiumModBase.correctBlendMode();
                float f = state;
                if (f > 0.0F) {
                    f = Mth.clamp(f, 0.0F, 1.0F);
                    guiGraphics.setColor(f, 0.0F, 0.0F, 1.0F);
                } else {
                    float g = this.vignetteBrightness;
                    g = Mth.clamp(g, 0.0F, 1.0F);
                    guiGraphics.setColor(0, 0, 0, vignetteBrightness);
                }

                guiGraphics.blit(FAST_VIGNETTE_LOCATION, 0, 0, -90, 0.0F, 0.0F, this.screenWidth, this.screenHeight,
                        this.screenWidth, this.screenHeight);
                RenderSystem.depthMask(true);
                RenderSystem.enableDepthTest();
                guiGraphics.setColor(1.0F, 1.0F, 1.0F, 1.0F);
                RenderSystem.defaultBlendFunc();
            } else {
                operation.call(gui, guiGraphics, entity);
            }
        }
        vignetteBuffer.renderEnd();
    }

}
