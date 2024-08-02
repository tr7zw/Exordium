package dev.tr7zw.exordium.access;

import dev.tr7zw.exordium.render.LegacyBuffer;
import net.minecraft.client.gui.GuiGraphics;

public interface VanillaBufferAccess {

    interface DebugOverlayAccess extends VanillaBufferAccess {

        LegacyBuffer getDebugOverlayBuffer();

    }

    interface CrosshairOverlayAccess extends VanillaBufferAccess {

        LegacyBuffer exordium_getCrosshairOverlayBuffer();
    }

    interface ExperienceBarOverlayAccess extends VanillaBufferAccess {

        LegacyBuffer getExperienceBarOverlayBuffer();
    }

    interface PlayerListOverlayAccess extends VanillaBufferAccess {

        LegacyBuffer getPlayerListOverlayBuffer();
    }

    interface ScoreBoardOverlayAccess extends VanillaBufferAccess {

        LegacyBuffer getScoreBoardOverlayBuffer();
    }

    interface VignetteOverlayAccess extends VanillaBufferAccess {

        LegacyBuffer getVignetteOverlayBuffer();

        void renderCustomVignette(GuiGraphics guiGraphics);
    }

    interface HotbarOverlayAccess extends VanillaBufferAccess {

        LegacyBuffer getHotbarOverlayBuffer();
    }

    interface BossHealthOverlayAccess extends VanillaBufferAccess {
        LegacyBuffer getHotbarOverlayBuffer();
    }

}
