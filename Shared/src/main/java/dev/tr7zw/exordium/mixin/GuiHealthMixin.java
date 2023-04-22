package dev.tr7zw.exordium.mixin;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import com.mojang.blaze3d.platform.GlStateManager;
import com.mojang.blaze3d.vertex.PoseStack;

import dev.tr7zw.exordium.ExordiumModBase;
import dev.tr7zw.exordium.util.BufferedComponent;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Gui;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.LivingEntity;

@Mixin(Gui.class)
public abstract class GuiHealthMixin {

    @Shadow
    private Minecraft minecraft;
    @Shadow
    private long healthBlinkTime;
    @Shadow
    private int lastHealth;
    @Shadow
    private int displayHealth;
    @Shadow
    private int tickCount;
    
    private boolean healthBlinking;
    private int lastRenderedHealth;
    private int lastDisplayHealth;
    private int lastArmorValue;
    private int lastVehicleHearts;
    private int lastAirSupply;
    private float lastSaturation;
    private float lastRenderedTick;
    private float lastPlayerHealth;
    private boolean hadVisualEffects;

    private BufferedComponent healthBuffer = new BufferedComponent(ExordiumModBase.instance.config.healthSettings) {

        @Override
        public boolean needsRender() {
            boolean hasVisualEffects = minecraft.player.hasEffect(MobEffects.HUNGER) || minecraft.player.hasEffect(MobEffects.REGENERATION);
            boolean blinking = (healthBlinkTime > tickCount && (healthBlinkTime - tickCount) / 3L % 2L == 1L);
            return healthBlinking != blinking ||
            lastRenderedHealth != lastHealth ||
            lastDisplayHealth != displayHealth ||
            lastArmorValue != minecraft.player.getArmorValue() ||
            lastVehicleHearts != getVehicleMaxHearts(getPlayerVehicleWithHealth()) ||
            lastAirSupply != minecraft.player.getAirSupply() ||
            lastSaturation != minecraft.player.getFoodData().getSaturationLevel() ||
            (hasVisualEffects || (hasVisualEffects != hadVisualEffects && lastRenderedTick != tickCount)) ||
            lastPlayerHealth != minecraft.player.getHealth();
        }

        @Override
        public void captureState() {
            healthBlinking = (healthBlinkTime > tickCount && (healthBlinkTime - tickCount) / 3L % 2L == 1L);
            lastRenderedHealth = lastHealth;
            lastDisplayHealth = displayHealth;
            lastArmorValue = minecraft.player.getArmorValue();
            lastVehicleHearts = getVehicleMaxHearts(getPlayerVehicleWithHealth());
            lastAirSupply = minecraft.player.getAirSupply();
            lastSaturation = minecraft.player.getFoodData().getSaturationLevel();
            lastRenderedTick = tickCount;
            lastPlayerHealth = minecraft.player.getHealth();
            hadVisualEffects = minecraft.player.hasEffect(MobEffects.HUNGER) || minecraft.player.hasEffect(MobEffects.REGENERATION);
        }
    };

    @Inject(method = "renderPlayerHealth", at = @At("HEAD"), cancellable = true)
    private void renderPlayerHealth(PoseStack poseStack, CallbackInfo ci) {
        if (healthBuffer.render()) {
            ci.cancel();
        }
    }

    @Inject(method = "renderPlayerHealth", at = @At("RETURN"), cancellable = true)
    private void renderPlayerHealthEnd(PoseStack poseStack, CallbackInfo ci) {
        healthBuffer.renderEnd();
    }
    
    @Shadow
    protected abstract LivingEntity getPlayerVehicleWithHealth();
    
    @Shadow
    protected abstract int getVehicleMaxHearts(LivingEntity livingEntity);

}