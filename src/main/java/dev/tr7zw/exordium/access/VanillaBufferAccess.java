package dev.tr7zw.exordium.access;

import dev.tr7zw.exordium.render.LegacyBuffer;
import net.minecraft.client.gui.GuiGraphics;

public interface VanillaBufferAccess {

    interface BossHealthOverlayAccess extends VanillaBufferAccess {
        LegacyBuffer getHotbarOverlayBuffer();
    }

}
