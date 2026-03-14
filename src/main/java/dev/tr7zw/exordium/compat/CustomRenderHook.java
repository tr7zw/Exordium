package dev.tr7zw.exordium.compat;

import net.minecraft.client.DeltaTracker;
import net.minecraft.client.gui.GuiGraphicsExtractor;

public interface CustomRenderHook {

    public void render(GuiGraphicsExtractor guiGraphics, DeltaTracker partialTicks);

}
