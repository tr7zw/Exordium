package dev.tr7zw.exordium.mixin;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import com.mojang.blaze3d.vertex.PoseStack;

import dev.tr7zw.exordium.ExordiumModBase;
import dev.tr7zw.exordium.util.BufferedComponent;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Gui;

@Mixin(Gui.class)
public class GuiExperienceMixin {

    @Shadow
    private Minecraft minecraft;
    private int lastlevel = 0;
    private float lastprogress = 0;

    private BufferedComponent experienceBuffer = new BufferedComponent(ExordiumModBase.instance.config.experienceSettings) {

        @Override
        public boolean needsRender() {
            return minecraft.player.experienceLevel != lastlevel || minecraft.player.experienceProgress != lastprogress;
        }

        @Override
        public void captureState() {
            lastlevel = minecraft.player.experienceLevel;
            lastprogress = minecraft.player.experienceProgress;
        }
    };

    @Inject(method = "renderExperienceBar", at = @At("HEAD"), cancellable = true)
    private void renderExperienceBar(PoseStack poseStack, int i, CallbackInfo ci) {
        if (experienceBuffer.render()) {
            ci.cancel();
        }
    }

    @Inject(method = "renderExperienceBar", at = @At("RETURN"), cancellable = true)
    private void renderExperienceBarEnd(PoseStack poseStack, int i, CallbackInfo ci) {
        experienceBuffer.renderEnd();
    }

}
