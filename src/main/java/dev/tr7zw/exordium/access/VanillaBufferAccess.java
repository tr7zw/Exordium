package dev.tr7zw.exordium.access;

import dev.tr7zw.exordium.util.BufferedComponent;
import net.minecraft.client.gui.GuiGraphics;

public interface VanillaBufferAccess {

    public interface DebugOverlayAccess extends VanillaBufferAccess {

        public BufferedComponent getDebugOverlayBuffer();

    }

    public interface ChatOverlayAccess extends VanillaBufferAccess {

        public BufferedComponent getChatOverlayBuffer();

    }

    public interface CrosshairOverlayAccess extends VanillaBufferAccess {

        public BufferedComponent exordium_getCrosshairOverlayBuffer();
    }

    public interface ExperienceBarOverlayAccess extends VanillaBufferAccess {

        public BufferedComponent getExperienceBarOverlayBuffer();
    }

    public interface PlayerListOverlayAccess extends VanillaBufferAccess {

        public BufferedComponent getPlayerListOverlayBuffer();
    }

    public interface ScoreBoardOverlayAccess extends VanillaBufferAccess {

        public BufferedComponent getScoreBoardOverlayBuffer();
    }

    public interface VignetteOverlayAccess extends VanillaBufferAccess {

        public BufferedComponent getVignetteOverlayBuffer();

        public void renderCustomVignette(GuiGraphics guiGraphics);
    }

    public interface HotbarOverlayAccess extends VanillaBufferAccess {

        public BufferedComponent getHotbarOverlayBuffer();
    }

}
