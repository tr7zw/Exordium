package dev.tr7zw.exordium.mixin;

import java.util.Objects;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;

import dev.tr7zw.exordium.ExordiumModBase;
import dev.tr7zw.exordium.access.VanillaBufferAccess.ScoreBoardOverlayAccess;
import dev.tr7zw.exordium.render.BufferedComponent;
import dev.tr7zw.exordium.render.LegacyBuffer;
import dev.tr7zw.exordium.util.ScoreboardHelper;
import dev.tr7zw.exordium.util.ScoreboardHelper.ScoreboardState;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.world.scores.Objective;

@Mixin(Gui.class)
public class ScoreboardMixin implements ScoreBoardOverlayAccess {

    @Shadow
    private Minecraft minecraft;
    private String scoreboardState = null;

    private LegacyBuffer scoreboardBuffer = new LegacyBuffer(true,
            () -> ExordiumModBase.instance.config.scoreboardSettings) {

        @Override
        public boolean shouldRenderNextCappedFrame() {
            ScoreboardState cur = ScoreboardHelper.getScoreboardData();
            return !Objects.equals(scoreboardState, "" + cur); // dirty workaround
        }

        @Override
        public void captureState() {
            scoreboardState = "" + ScoreboardHelper.getScoreboardData();
        }
    };

    @WrapOperation(method = "renderScoreboardSidebar", at = {
            @At(value = "INVOKE", target = "Lnet/minecraft/client/gui/Gui;displayScoreboardSidebar(Lnet/minecraft/client/gui/GuiGraphics;Lnet/minecraft/world/scores/Objective;)V"), })
    private void displayScoreboardSidebarWrapper(Gui gui, GuiGraphics guiGraphics, Objective objective,
            final Operation<Void> operation) {
        if (!scoreboardBuffer.render()) {
            operation.call(gui, guiGraphics, objective);
        }
        scoreboardBuffer.renderEnd();
    }

    @Override
    public LegacyBuffer getScoreBoardOverlayBuffer() {
        return scoreboardBuffer;
    }

}
