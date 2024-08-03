package dev.tr7zw.exordium.access;

import dev.tr7zw.exordium.render.LegacyBuffer;
import net.minecraft.client.gui.GuiGraphics;

public interface VanillaBufferAccess {

    interface PlayerListOverlayAccess extends VanillaBufferAccess {

        LegacyBuffer getPlayerListOverlayBuffer();
    }

    interface BossHealthOverlayAccess extends VanillaBufferAccess {
        LegacyBuffer getHotbarOverlayBuffer();
    }

}
