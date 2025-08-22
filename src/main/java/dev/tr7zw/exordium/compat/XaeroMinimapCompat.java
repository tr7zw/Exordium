package dev.tr7zw.exordium.compat;

import net.minecraft.client.DeltaTracker;
import net.minecraft.client.gui.GuiGraphics;
import xaero.common.core.XaeroMinimapCore;

public class XaeroMinimapCompat implements CustomRenderHook {

    @Override
    public void render(GuiGraphics guiGraphics, DeltaTracker partialTicks) {
        //#if FABRIC || NEOFORGE
        XaeroMinimapCore.handleRenderModOverlay(guiGraphics, partialTicks);
        //#else
        //$$ XaeroMinimapCore.handleRenderModOverlay(guiGraphics, partialTicks.getGameTimeDeltaPartialTick(true));
        //#endif
    }

}
