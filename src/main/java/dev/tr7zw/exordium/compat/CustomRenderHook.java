package dev.tr7zw.exordium.compat;

import net.minecraft.client.DeltaTracker;
import net.minecraft.client.gui.GuiGraphics;

public interface CustomRenderHook {

    public void render(GuiGraphics guiGraphics, DeltaTracker partialTicks);

}
