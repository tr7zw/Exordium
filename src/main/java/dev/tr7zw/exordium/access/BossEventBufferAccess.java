package dev.tr7zw.exordium.access;

import dev.tr7zw.exordium.util.BufferedComponent;

public interface BossEventBufferAccess {
    boolean exordium_needsRender();

    void exordium_captureState();
}
