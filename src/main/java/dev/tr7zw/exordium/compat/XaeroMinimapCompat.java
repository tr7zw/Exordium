package dev.tr7zw.exordium.compat;

import net.minecraft.client.DeltaTracker;
import net.minecraft.client.gui.GuiGraphics;
import xaero.common.core.XaeroMinimapCore;

public class XaeroMinimapCompat implements CustomRenderHook {

    @Override
    public void render(GuiGraphics guiGraphics, DeltaTracker partialTicks) {
        XaeroMinimapCore.handleRenderModOverlay(guiGraphics, partialTicks);
    }

}
