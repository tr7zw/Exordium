package dev.tr7zw.exordium.compat;

import net.minecraft.client.DeltaTracker;
import net.minecraft.client.gui.GuiGraphicsExtractor;

public class XaeroMinimapCompat implements CustomRenderHook {

    @Override
    public void render(GuiGraphicsExtractor guiGraphics, DeltaTracker partialTicks) {
        //? if < 26.0 {
        /*
        xaero.common.core.XaeroMinimapCore.handleRenderModOverlay(guiGraphics, partialTicks);
         *///? }
    }

}
