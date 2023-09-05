package dev.tr7zw.exordium.access;

import dev.tr7zw.exordium.util.BufferedComponent;

public interface VanillaBufferAccess {

    public interface DebugOverlayAccess extends VanillaBufferAccess {
        
        public BufferedComponent getDebugOverlayBuffer();
        
    }
    
}
