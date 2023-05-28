package dev.tr7zw.exordium.access;

import dev.tr7zw.exordium.util.BufferedComponent;

public interface ChatAccess {

    public void updateState(int tickCount);
    public BufferedComponent getBufferedComponent();
    
}
