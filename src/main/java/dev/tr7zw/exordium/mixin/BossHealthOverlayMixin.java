package dev.tr7zw.exordium.mixin;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import dev.tr7zw.exordium.access.BossEventBufferAccess;
import dev.tr7zw.exordium.util.BufferedComponent;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.BossHealthOverlay;
import net.minecraft.world.BossEvent;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(BossHealthOverlay.class)
public class BossHealthOverlayMixin {
    @WrapOperation(method = "render", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/gui/components/BossHealthOverlay;drawBar(Lnet/minecraft/client/gui/GuiGraphics;IILnet/minecraft/world/BossEvent;)V"))
    private void wrapBossHealthDrawBar(BossHealthOverlay overlay, GuiGraphics guiGraphics, int i, int j,
            BossEvent event, final Operation<Void> operation) {
        BufferedComponent buffer = ((BossEventBufferAccess) event).exordium_getBuffered();

        if (!buffer.render()) {
            operation.call(overlay, guiGraphics, i, j, event);
        }
        buffer.renderEnd();
    }
}
