package dev.tr7zw.exordium.buffers;

import dev.tr7zw.exordium.ExordiumModBase;
import dev.tr7zw.exordium.util.BufferedComponent;

public class BufferManager {

    private BufferedComponent debugBufferedComponent = new BufferedComponent(true,
            () -> ExordiumModBase.instance.config.debugScreenSettings) {

        @Override
        public boolean needsRender() {
            return true;
        }

        @Override
        public void captureState() {
        }
    };
    
    public BufferedComponent getDebugBuffer() {
        return debugBufferedComponent;
    }

}
