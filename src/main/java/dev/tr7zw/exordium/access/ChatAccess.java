package dev.tr7zw.exordium.access;

import dev.tr7zw.exordium.access.VanillaBufferAccess.ChatOverlayAccess;

public interface ChatAccess extends ChatOverlayAccess {

    void updateState(int tickCount);

}
