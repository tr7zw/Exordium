package dev.tr7zw.exordium.mixin;

import dev.tr7zw.exordium.ExordiumModBase;
import dev.tr7zw.exordium.access.BossEventBufferAccess;
import dev.tr7zw.exordium.access.VanillaBufferAccess;
import dev.tr7zw.exordium.util.BufferedComponent;
import net.minecraft.client.gui.components.BossHealthOverlay;
import net.minecraft.client.gui.components.LerpingBossEvent;
import net.minecraft.network.protocol.game.ClientboundBossEventPacket;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.Map;
import java.util.UUID;

@Mixin(BossHealthOverlay.class)
public class BossHealthOverlayMixin implements VanillaBufferAccess.BossHealthOverlayAccess {
    /*
     * @WrapOperation(method = "render", at = @At(value = "INVOKE", target =
     * "Lnet/minecraft/client/gui/components/BossHealthOverlay;drawBar(Lnet/minecraft/client/gui/GuiGraphics;IILnet/minecraft/world/BossEvent;)V"
     * )) private void wrapBossHealthDrawBar(BossHealthOverlay overlay, GuiGraphics
     * guiGraphics, int i, int j, BossEvent event, final Operation<Void> operation)
     * { BufferedComponent buffer = ((BossEventBufferAccess)
     * event).exordium_getBuffered();
     * 
     * if (!buffer.render()) { operation.call(overlay, guiGraphics, i, j, event); }
     * buffer.renderEnd(); }
     */

    @Shadow
    @Final
    Map<UUID, LerpingBossEvent> events;
    @Unique
    private final BufferedComponent chatBufferedComponent = new BufferedComponent(
            () -> ExordiumModBase.instance.config.bossbarSettings) {

        @Override
        public boolean shouldRenderNextCappedFrame() {
            for (LerpingBossEvent value : BossHealthOverlayMixin.this.events.values()) {
                if (((BossEventBufferAccess) value).exordium_needsRender())
                    return true;
            }
            return false;
        }

        @Override
        public void captureState() {
            for (LerpingBossEvent value : BossHealthOverlayMixin.this.events.values()) {
                ((BossEventBufferAccess) value).exordium_captureState();
            }
        }
    };

    @Unique
    boolean outdated = false;

    @Override
    public BufferedComponent getHotbarOverlayBuffer() {
        return chatBufferedComponent;
    }

    @Inject(method = "update", at = @At("RETURN"))
    private void onUpdate(ClientboundBossEventPacket packet, CallbackInfo ci) {
        outdated = true;
    }
}
