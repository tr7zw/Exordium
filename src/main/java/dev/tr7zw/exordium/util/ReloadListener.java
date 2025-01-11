package dev.tr7zw.exordium.util;

import java.util.function.Supplier;

public class ReloadListener implements Supplier<Boolean> {

    private int reloadCount = 0;

    @Override
    public Boolean get() {
        if (reloadCount != ReloadTracker.getReloadCount()) {
            reloadCount = ReloadTracker.getReloadCount();
            return true;
        }
        return false;
    }

}
