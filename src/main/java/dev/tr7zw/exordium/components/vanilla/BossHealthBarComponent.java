package dev.tr7zw.exordium.components.vanilla;

import java.util.Map;
import java.util.UUID;

import dev.tr7zw.exordium.access.BossEventBufferAccess;
import dev.tr7zw.exordium.access.BossOverlayAccess;
import dev.tr7zw.exordium.components.BufferComponent;
import dev.tr7zw.util.NMSHelper;
import lombok.Getter;
import net.minecraft.client.gui.components.LerpingBossEvent;
import net.minecraft.resources.ResourceLocation;

public class BossHealthBarComponent implements BufferComponent<BossOverlayAccess> {

    @Getter
    private static final ResourceLocation id = NMSHelper.getResourceLocation("minecraft", "boss_bar");

    @Override
    public void captureState(BossOverlayAccess context) {
        for (LerpingBossEvent value : context.getEvents().values()) {
            ((BossEventBufferAccess) value).exordium_captureState();
        }
    }

    @Override
    public boolean hasChanged(int tickCount, BossOverlayAccess context) {
        for (LerpingBossEvent value : context.getEvents().values()) {
            if (((BossEventBufferAccess) value).exordium_needsRender())
                return true;
        }
        return false;
    }

}
