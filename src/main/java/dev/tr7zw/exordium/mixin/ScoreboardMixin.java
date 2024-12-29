package dev.tr7zw.exordium.mixin;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;

import dev.tr7zw.exordium.ExordiumModBase;
import dev.tr7zw.exordium.components.BufferInstance;
import dev.tr7zw.exordium.components.vanilla.ScoreboardComponent;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.world.scores.Objective;

@Mixin(Gui.class)
public class ScoreboardMixin {

    @WrapOperation(method = "renderScoreboardSidebar", at = {
            @At(value = "INVOKE", target = "Lnet/minecraft/client/gui/Gui;displayScoreboardSidebar(Lnet/minecraft/client/gui/GuiGraphics;Lnet/minecraft/world/scores/Objective;)V"), })
    private void displayScoreboardSidebarWrapper(Gui gui, GuiGraphics guiGraphics, Objective objective,
            final Operation<Void> operation) {
        BufferInstance<Void> buffer = ExordiumModBase.instance.getBufferManager()
                .getBufferInstance(ScoreboardComponent.getId(), Void.class);
        if (!buffer.renderBuffer(0, null, guiGraphics)) {
            operation.call(gui, guiGraphics, objective);
        }
        buffer.postRender(null, guiGraphics);
    }

}
