package dev.tr7zw.exordium.components.vanilla;

import dev.tr7zw.exordium.components.BufferComponent;
import dev.tr7zw.util.NMSHelper;
import lombok.Getter;
import net.minecraft.client.Minecraft;
import net.minecraft.resources.ResourceLocation;

public class ExperienceComponent implements BufferComponent<Void> {

    @Getter
    private static final ResourceLocation id = NMSHelper.getResourceLocation("minecraft", "experience_bar");
    private static final Minecraft minecraft = Minecraft.getInstance();

    private int lastlevel = 0;
    private float lastprogress = 0;

    @Override
    public void captureState(Void context) {
        lastlevel = minecraft.player.experienceLevel;
        lastprogress = minecraft.player.experienceProgress;
    }

    @Override
    public boolean hasChanged(int tickCount, Void context) {
        return minecraft.player.experienceLevel != lastlevel || minecraft.player.experienceProgress != lastprogress;
    }

}
