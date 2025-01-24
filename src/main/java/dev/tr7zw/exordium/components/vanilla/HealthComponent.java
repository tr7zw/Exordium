package dev.tr7zw.exordium.components.vanilla;

import dev.tr7zw.exordium.access.HealthAccess;
import dev.tr7zw.exordium.components.BufferComponent;
import dev.tr7zw.exordium.mixin.FoodDataAccessor;
import dev.tr7zw.util.NMSHelper;
import lombok.Getter;
import net.minecraft.client.Minecraft;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.LivingEntity;

public class HealthComponent implements BufferComponent<HealthAccess> {

    private static final Minecraft minecraft = Minecraft.getInstance();
    @Getter
    private static final ResourceLocation id = NMSHelper.getResourceLocation("minecraft", "health");

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

    @Override
    public void captureState(HealthAccess context) {
        LivingEntity vehicle = context.getExordiumPlayerVehicleWithHealth();
        healthBlinking = (context.getHealthBlinkTime() > context.getTickCount()
                && (context.getHealthBlinkTime() - context.getTickCount()) / 3L % 2L == 1L);
        lastRenderedHealth = context.getLastHealth();
        lastDisplayHealth = context.getDisplayHealth();
        lastArmorValue = minecraft.player.getArmorValue();
        lastMaxVehicleHearts = context.getExordiumVehicleMaxHearts(vehicle);
        lastVehicleHearts = vehicle == null ? -1 : vehicle.getHealth();
        lastAirSupply = minecraft.player.getAirSupply();
        lastSaturation = minecraft.player.getFoodData().getSaturationLevel();
        lastRenderedTick = context.getTickCount();
        lastPlayerHealth = minecraft.player.getHealth();
        lastPlayerAbsorption = minecraft.player.getAbsorptionAmount();
        lastFoodLevel = minecraft.player.getFoodData().getFoodLevel();
        lastExhaustionLevel = ((FoodDataAccessor) minecraft.player.getFoodData()).getExhaustionLevel();
        hadVisualEffects = minecraft.player.hasEffect(MobEffects.HUNGER)
                || minecraft.player.hasEffect(MobEffects.REGENERATION);
    }

    @Override
    public boolean hasChanged(HealthAccess context) {
        boolean hasVisualEffects = minecraft.player.hasEffect(MobEffects.HUNGER)
                || minecraft.player.hasEffect(MobEffects.REGENERATION);

        boolean blinking = (context.getHealthBlinkTime() > context.getTickCount()
                && (context.getHealthBlinkTime() - context.getTickCount()) / 3L % 2L == 1L);
        LivingEntity vehicle = context.getExordiumPlayerVehicleWithHealth();
        return healthBlinking != blinking || lastRenderedHealth != context.getLastHealth()
                || lastDisplayHealth != context.getDisplayHealth() || lastArmorValue != minecraft.player.getArmorValue()
                || lastMaxVehicleHearts != context.getExordiumVehicleMaxHearts(vehicle)
                || lastVehicleHearts != (vehicle == null ? -1 : vehicle.getHealth())
                || lastAirSupply != minecraft.player.getAirSupply()
                || lastSaturation != minecraft.player.getFoodData().getSaturationLevel()
                || (hasVisualEffects
                        || (hasVisualEffects != hadVisualEffects && lastRenderedTick != context.getTickCount()))
                || lastFoodLevel != minecraft.player.getFoodData().getFoodLevel()
                || lastExhaustionLevel != ((FoodDataAccessor) minecraft.player.getFoodData()).getExhaustionLevel()
                || lastPlayerHealth != minecraft.player.getHealth() || Mth.ceil(lastPlayerHealth) <= 4
                || lastPlayerAbsorption != minecraft.player.getAbsorptionAmount();
    }

}
