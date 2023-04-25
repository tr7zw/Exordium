package dev.tr7zw.exordium.mixin;

import java.util.Objects;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import com.mojang.blaze3d.vertex.PoseStack;

import dev.tr7zw.exordium.ExordiumModBase;
import dev.tr7zw.exordium.util.BufferedComponent;
import dev.tr7zw.exordium.util.ScoreboardHelper;
import dev.tr7zw.exordium.util.ScoreboardHelper.ScoreboardState;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Gui;
import net.minecraft.world.scores.Objective;

@Mixin(Gui.class)
public class ScoreboardMixin {

    @Shadow
    private Minecraft minecraft;
    private String scoreboardState = null;

    private BufferedComponent scoreboardBuffer = new BufferedComponent(true, ExordiumModBase.instance.config.scoreboardSettings) {

        @Override
        public boolean needsRender() {
            ScoreboardState cur = ScoreboardHelper.getScoreboardData();
            return !Objects.equals(scoreboardState, ""+cur); // dirty workaround
        }

        @Override
        public void captureState() {
            scoreboardState = ""+ScoreboardHelper.getScoreboardData();
        }
    };

    @Inject(method = "displayScoreboardSidebar", at = @At("HEAD"), cancellable = true)
    private void displayScoreboardSidebar(PoseStack poseStack, Objective objective, CallbackInfo ci) {
        if (scoreboardBuffer.render()) {
            ci.cancel();
        }
    }

    @Inject(method = "displayScoreboardSidebar", at = @At("RETURN"), cancellable = true)
    private void displayScoreboardSidebarEnd(PoseStack poseStack, Objective objective, CallbackInfo ci) {
        scoreboardBuffer.renderEnd();
    }

}
