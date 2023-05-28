package dev.tr7zw.exordium.mixin;

import java.util.Objects;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
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

    @WrapOperation(method = "render", at = {
            @At(value = "INVOKE", target = "Lnet/minecraft/client/gui/Gui;displayScoreboardSidebar(Lcom/mojang/blaze3d/vertex/PoseStack;Lnet/minecraft/world/scores/Objective;)V"),
    })
    private void displayScoreboardSidebarWrapper(Gui gui, PoseStack poseStack, Objective objective, final Operation<Void> operation) {
        if (!scoreboardBuffer.render()) {
            operation.call(gui, poseStack, objective);
        }
        scoreboardBuffer.renderEnd();
    }

}
