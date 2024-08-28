package dev.tr7zw.exordium.components.vanilla;

import dev.tr7zw.exordium.components.BufferComponent;
import dev.tr7zw.exordium.versionless.config.Config.ComponentSettings;
import dev.tr7zw.util.NMSHelper;
import lombok.Getter;
import net.minecraft.client.Minecraft;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.border.WorldBorder;

public class VignetteComponent implements BufferComponent<Float> {

    private static final Minecraft MINECRAFT = Minecraft.getInstance();
    @Getter
    private static final ResourceLocation id = NMSHelper.getResourceLocation("minecraft", "vignette");

    private float exordium_state = 0f;
    private float exordium_lastVignetteBrightness = 1.0F;

    @Override
    public void captureState(Float vignetteBrightness) {
        WorldBorder worldBorder = MINECRAFT.level.getWorldBorder();
        float f = (float) worldBorder.getDistanceToBorder(Minecraft.getInstance().getCameraEntity());
        double d = Math.min(worldBorder.getLerpSpeed() * (double) worldBorder.getWarningTime() * 1000.0D,
                Math.abs(worldBorder.getLerpTarget() - worldBorder.getSize()));
        double e = Math.max((double) worldBorder.getWarningBlocks(), d);
        if ((double) f < e) {
            f = 1.0F - (float) ((double) f / e);
        } else {
            f = 0.0F;
        }
        exordium_state = f;
        exordium_lastVignetteBrightness = vignetteBrightness;
    }

    @Override
    public boolean hasChanged(int tickCount, Float vignetteBrightness) {
        if (exordium_lastVignetteBrightness != vignetteBrightness) {
            return true;
        }
        WorldBorder worldBorder = MINECRAFT.level.getWorldBorder();
        float f = (float) worldBorder.getDistanceToBorder(Minecraft.getInstance().getCameraEntity());
        double d = Math.min(worldBorder.getLerpSpeed() * (double) worldBorder.getWarningTime() * 1000.0D,
                Math.abs(worldBorder.getLerpTarget() - worldBorder.getSize()));
        double e = Math.max((double) worldBorder.getWarningBlocks(), d);
        if ((double) f < e) {
            f = 1.0F - (float) ((double) f / e);
        } else {
            f = 0.0F;
        }
        return exordium_state == f;
    }

    @Override
    public boolean enabled(ComponentSettings settings) {
        if (MINECRAFT.screen != null) {
            // during screens disable due to issues with the blur
            return false;
        }
        return BufferComponent.super.enabled(settings);
    }

}
