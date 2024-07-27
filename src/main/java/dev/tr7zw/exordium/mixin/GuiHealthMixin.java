package dev.tr7zw.exordium.mixin;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;

import dev.tr7zw.exordium.ExordiumModBase;
import dev.tr7zw.exordium.util.BufferedComponent;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.util.Mth;
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

    private boolean renderingMountHealth = false;

    private boolean healthBlinking;
    private int lastRenderedHealth;
    private int lastDisplayHealth;
    private int lastArmorValue;
    private float lastVehicleHearts;
    private int lastMaxVehicleHearts;
    private int lastAirSupply;
    private float lastSaturation;
    private float lastRenderedTick;
    private float lastPlayerHealth;
    private float lastFoodLevel;
    private float lastExhaustionLevel;
    private float lastPlayerAbsorption;
    private boolean hadVisualEffects;

    private BufferedComponent healthBuffer = new BufferedComponent(
            () -> ExordiumModBase.instance.config.healthSettings) {

        @Override
        public boolean shouldRenderNextCappedFrame() {
            return false;
        }

        @Override
        public boolean shouldForceRender() {
            boolean hasVisualEffects = minecraft.player.hasEffect(MobEffects.HUNGER)
                    || minecraft.player.hasEffect(MobEffects.REGENERATION);

            boolean blinking = (healthBlinkTime > tickCount && (healthBlinkTime - tickCount) / 3L % 2L == 1L);
            LivingEntity vehicle = getPlayerVehicleWithHealth();
            return healthBlinking != blinking || lastRenderedHealth != lastHealth || lastDisplayHealth != displayHealth
                    || lastArmorValue != minecraft.player.getArmorValue()
                    || lastMaxVehicleHearts != getVehicleMaxHearts(vehicle)
                    || lastVehicleHearts != (vehicle == null ? -1 : vehicle.getHealth())
                    || lastAirSupply != minecraft.player.getAirSupply()
                    || lastSaturation != minecraft.player.getFoodData().getSaturationLevel()
                    || (hasVisualEffects || (hasVisualEffects != hadVisualEffects && lastRenderedTick != tickCount))
                    || lastFoodLevel != minecraft.player.getFoodData().getFoodLevel()
                    || lastExhaustionLevel != minecraft.player.getFoodData().getExhaustionLevel()
                    || lastPlayerHealth != minecraft.player.getHealth() || Mth.ceil(lastPlayerHealth) <= 4
                    || lastPlayerAbsorption != minecraft.player.getAbsorptionAmount();
        }

        @Override
        public void captureState() {
            LivingEntity vehicle = getPlayerVehicleWithHealth();
            healthBlinking = (healthBlinkTime > tickCount && (healthBlinkTime - tickCount) / 3L % 2L == 1L);
            lastRenderedHealth = lastHealth;
            lastDisplayHealth = displayHealth;
            lastArmorValue = minecraft.player.getArmorValue();
            lastMaxVehicleHearts = getVehicleMaxHearts(vehicle);
            lastVehicleHearts = vehicle == null ? -1 : vehicle.getHealth();
            lastAirSupply = minecraft.player.getAirSupply();
            lastSaturation = minecraft.player.getFoodData().getSaturationLevel();
            lastRenderedTick = tickCount;
            lastPlayerHealth = minecraft.player.getHealth();
            lastPlayerAbsorption = minecraft.player.getAbsorptionAmount();
            lastFoodLevel = minecraft.player.getFoodData().getFoodLevel();
            lastExhaustionLevel = minecraft.player.getFoodData().getExhaustionLevel();
            hadVisualEffects = minecraft.player.hasEffect(MobEffects.HUNGER)
                    || minecraft.player.hasEffect(MobEffects.REGENERATION);
        }
    };

    @WrapOperation(method = "renderHotbarAndDecorations", at = {
            @At(value = "INVOKE", target = "Lnet/minecraft/client/gui/Gui;renderPlayerHealth(Lnet/minecraft/client/gui/GuiGraphics;)V"), })
    private void renderPlayerHealthWrapper(Gui gui, GuiGraphics guiGraphics, final Operation<Void> operation) {
        if (!healthBuffer.render()) {
            operation.call(gui, guiGraphics);
            renderingMountHealth = true;
            renderVehicleHealth(guiGraphics);
            renderingMountHealth = false;
        }
        healthBuffer.renderEnd();
    }

    @WrapOperation(method = "renderHotbarAndDecorations", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/gui/Gui;renderVehicleHealth(Lnet/minecraft/client/gui/GuiGraphics;)V"))
    private void renderVehicleHealthHead(Gui gui, GuiGraphics guiGraphics, final Operation<Void> operation) {
        if (renderingMountHealth || !ExordiumModBase.instance.config.healthSettings.isEnabled()
                || minecraft.player.isCreative()) {
            // prevent rendering multiple times, just render into the texture
            operation.call(gui, guiGraphics);
        }
    }

    @Shadow
    public abstract void renderVehicleHealth(GuiGraphics guiGraphics);

    @Shadow
    protected abstract LivingEntity getPlayerVehicleWithHealth();

    @Shadow
    protected abstract int getVehicleMaxHearts(LivingEntity livingEntity);

}