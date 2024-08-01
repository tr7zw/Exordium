package dev.tr7zw.exordium.access;

import dev.tr7zw.exordium.util.BufferedComponent;
import net.minecraft.client.gui.GuiGraphics;

public interface VanillaBufferAccess {

    interface DebugOverlayAccess extends VanillaBufferAccess {

        BufferedComponent getDebugOverlayBuffer();

    }

    interface CrosshairOverlayAccess extends VanillaBufferAccess {

        BufferedComponent exordium_getCrosshairOverlayBuffer();
    }

    interface ExperienceBarOverlayAccess extends VanillaBufferAccess {

        BufferedComponent getExperienceBarOverlayBuffer();
    }

    interface PlayerListOverlayAccess extends VanillaBufferAccess {

        BufferedComponent getPlayerListOverlayBuffer();
    }

    interface ScoreBoardOverlayAccess extends VanillaBufferAccess {

        BufferedComponent getScoreBoardOverlayBuffer();
    }

    interface VignetteOverlayAccess extends VanillaBufferAccess {

        BufferedComponent getVignetteOverlayBuffer();

        void renderCustomVignette(GuiGraphics guiGraphics);
    }

    interface HotbarOverlayAccess extends VanillaBufferAccess {

        BufferedComponent getHotbarOverlayBuffer();
    }

    interface BossHealthOverlayAccess extends VanillaBufferAccess {
        BufferedComponent getHotbarOverlayBuffer();
    }

}
