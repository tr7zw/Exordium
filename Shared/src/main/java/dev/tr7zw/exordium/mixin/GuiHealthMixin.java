package dev.tr7zw.exordium.mixin;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
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
    private float lastFoodLevel;
    private float lastExhaustionLevel;
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
            lastFoodLevel != minecraft.player.getFoodData().getFoodLevel() ||
            lastExhaustionLevel != minecraft.player.getFoodData().getExhaustionLevel() ||
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
            lastFoodLevel = minecraft.player.getFoodData().getFoodLevel();
            lastExhaustionLevel = minecraft.player.getFoodData().getExhaustionLevel();
            hadVisualEffects = minecraft.player.hasEffect(MobEffects.HUNGER) || minecraft.player.hasEffect(MobEffects.REGENERATION);
        }
    };

    @WrapOperation(method = "render", at = {
            @At(value = "INVOKE", target = "Lnet/minecraft/client/gui/Gui;renderPlayerHealth(Lcom/mojang/blaze3d/vertex/PoseStack;)V"),
    })
    private void renderPlayerHealthWrapper(Gui gui, PoseStack poseStack, final Operation<Void> operation) {
        if (!healthBuffer.render()) {
            operation.call(gui, poseStack);
        }
        healthBuffer.renderEnd();
    }
    
    @Shadow
    public abstract void renderPlayerHealth(PoseStack poseStack);
    
    @Shadow
    protected abstract LivingEntity getPlayerVehicleWithHealth();
    
    @Shadow
    protected abstract int getVehicleMaxHearts(LivingEntity livingEntity);

}