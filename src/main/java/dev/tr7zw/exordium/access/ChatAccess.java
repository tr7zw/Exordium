package dev.tr7zw.exordium.access;

import dev.tr7zw.exordium.access.VanillaBufferAccess.ChatOverlayAccess;

public interface ChatAccess extends ChatOverlayAccess {

    public void updateState(int tickCount);

}
