package dev.tr7zw.exordium.access;

import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.world.entity.LivingEntity;

public interface HealthAccess {

    void renderVehicleHealth(GuiGraphics guiGraphics);

    LivingEntity getExordiumPlayerVehicleWithHealth();

    int getExordiumVehicleMaxHearts(LivingEntity livingEntity);

    int getTickCount();

    int getDisplayHealth();

    int getLastHealth();

    long getHealthBlinkTime();

}
