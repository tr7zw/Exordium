package dev.tr7zw.exordium.components.vanilla;

import dev.tr7zw.exordium.components.BufferComponent;
import dev.tr7zw.util.NMSHelper;
import lombok.Getter;
import net.minecraft.resources.ResourceLocation;

public class DebugOverlayComponent implements BufferComponent<Void> {

    @Getter
    private static final ResourceLocation id = NMSHelper.getResourceLocation("minecraft", "debug_text");

    @Override
    public void captureState(Void context) {

    }

    @Override
    public boolean hasChanged(int tickCount, Void context) {
        return true;
    }

}
