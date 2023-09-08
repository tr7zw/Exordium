package dev.tr7zw.exordium.mixin;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;

import dev.tr7zw.exordium.ExordiumModBase;
import dev.tr7zw.exordium.access.VanillaBufferAccess.CrosshairOverlayAccess;
import dev.tr7zw.exordium.util.BufferedComponent;
import net.minecraft.client.AttackIndicatorStatus;
import net.minecraft.client.CameraType;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.world.entity.LivingEntity;

@Mixin(Gui.class)
public class CrosshairMixin implements CrosshairOverlayAccess {

    @Shadow
    private Minecraft minecraft;

    private boolean wasRenderingF3 = false;
    private float lastPitch = 0;
    private float lastYaw = 0;
    private float lastCooldown = 0;
    private boolean lastHighlight = false;
    private boolean lastHidden = false;

    private BufferedComponent crosshairBufferedComponent = new BufferedComponent(true,
            () -> ExordiumModBase.instance.config.crosshairSettings) {

        @Override
        public boolean needsRender() {
            if (wasRenderingF3 != minecraft.options.renderDebug) {
                return true;
            }
            if(lastHidden != ((minecraft.options.getCameraType() != CameraType.FIRST_PERSON) || minecraft.player.isSpectator())) {
                return true;
            }
            if (wasRenderingF3) {
                return lastPitch != minecraft.getCameraEntity().getXRot()
                        || lastYaw != minecraft.getCameraEntity().getYRot();
            }
            if (minecraft.options.attackIndicator().get() == AttackIndicatorStatus.CROSSHAIR) {
                float cooldown = minecraft.player.getAttackStrengthScale(0.0F);
                if(lastCooldown != cooldown) {
                    return true;
                }
                boolean flag = false;
                if (minecraft.crosshairPickEntity != null
                        && minecraft.crosshairPickEntity instanceof LivingEntity && cooldown >= 1.0F) {
                    flag = minecraft.player.getCurrentItemAttackStrengthDelay() > 5.0F;
                    flag &= minecraft.crosshairPickEntity.isAlive();
                }
                return flag != lastHighlight;
            }
            return false;
        }

        @Override
        public void captureState() {
            lastHidden = minecraft.options.getCameraType() != CameraType.FIRST_PERSON || minecraft.player.isSpectator();
            wasRenderingF3 = minecraft.options.renderDebug;
            lastPitch = minecraft.getCameraEntity().getXRot();
            lastYaw = minecraft.getCameraEntity().getYRot();
            lastCooldown = minecraft.player.getAttackStrengthScale(0.0F);
            boolean flag = false;
            if (minecraft.crosshairPickEntity != null
                    && minecraft.crosshairPickEntity instanceof LivingEntity && lastCooldown >= 1.0F) {
                flag = minecraft.player.getCurrentItemAttackStrengthDelay() > 5.0F;
                flag &= minecraft.crosshairPickEntity.isAlive();
            }
            lastHighlight = flag;
        }
    };

    @WrapOperation(method = "render", at = {
            @At(value = "INVOKE", target = "Lnet/minecraft/client/gui/Gui;renderCrosshair(Lnet/minecraft/client/gui/GuiGraphics;)V"),
    })
    private void renderCrosshairWrapper(Gui gui, GuiGraphics guiGraphics, final Operation<Void> operation) {
        if (!crosshairBufferedComponent.render()) {
            operation.call(gui, guiGraphics);
        }
        crosshairBufferedComponent.renderEnd();
    }

    @Override
    public BufferedComponent getCrosshairOverlayBuffer() {
        return crosshairBufferedComponent;
    }

}
