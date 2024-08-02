package dev.tr7zw.exordium.components.vanilla;

import dev.tr7zw.exordium.components.BufferComponent;
import dev.tr7zw.util.NMSHelper;
import lombok.Getter;
import net.minecraft.client.AttackIndicatorStatus;
import net.minecraft.client.CameraType;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.components.DebugScreenOverlay;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.LivingEntity;

public class CrosshairComponent implements BufferComponent<DebugScreenOverlay> {

    private static final Minecraft minecraft = Minecraft.getInstance();
    @Getter
    private static final ResourceLocation id = NMSHelper.getResourceLocation("minecraft", "crosshair");

    private boolean wasRenderingF3 = false;
    private float lastPitch = 0;
    private float lastYaw = 0;
    private float lastCooldown = 0;
    private boolean lastHighlight = false;
    private boolean lastHidden = false;

    @Override
    public void captureState(DebugScreenOverlay debugOverlay) {
        lastHidden = minecraft.options.getCameraType() != CameraType.FIRST_PERSON || minecraft.player.isSpectator();
        wasRenderingF3 = debugOverlay.showDebugScreen();
        lastPitch = minecraft.getCameraEntity().getXRot();
        lastYaw = minecraft.getCameraEntity().getYRot();
        lastCooldown = minecraft.player.getAttackStrengthScale(0.0F);
        boolean flag = false;
        if (minecraft.crosshairPickEntity != null && minecraft.crosshairPickEntity instanceof LivingEntity
                && lastCooldown >= 1.0F) {
            flag = minecraft.player.getCurrentItemAttackStrengthDelay() > 5.0F;
            flag &= minecraft.crosshairPickEntity.isAlive();
        }
        lastHighlight = flag;
    }

    @Override
    public boolean hasChanged(int tickCount, DebugScreenOverlay debugOverlay) {
        if (wasRenderingF3 != debugOverlay.showDebugScreen()) {
            return true;
        }
        if (lastHidden != ((minecraft.options.getCameraType() != CameraType.FIRST_PERSON)
                || minecraft.player.isSpectator())) {
            return true;
        }
        if (wasRenderingF3) {
            return lastPitch != minecraft.getCameraEntity().getXRot()
                    || lastYaw != minecraft.getCameraEntity().getYRot();
        }
        if (minecraft.options.attackIndicator().get() == AttackIndicatorStatus.CROSSHAIR) {
            float cooldown = minecraft.player.getAttackStrengthScale(0.0F);
            if (lastCooldown != cooldown) {
                return true;
            }
            boolean flag = false;
            if (minecraft.crosshairPickEntity != null && minecraft.crosshairPickEntity instanceof LivingEntity
                    && cooldown >= 1.0F) {
                flag = minecraft.player.getCurrentItemAttackStrengthDelay() > 5.0F;
                flag &= minecraft.crosshairPickEntity.isAlive();
            }
            return flag != lastHighlight;
        }
        return false;
    }

}
