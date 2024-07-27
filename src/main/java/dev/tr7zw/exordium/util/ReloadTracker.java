package dev.tr7zw.exordium.util;

import lombok.Getter;
import lombok.experimental.UtilityClass;

/**
 * Helper to keep track of resource pack changes
 */
@UtilityClass
public class ReloadTracker {
    @Getter
    private static int reloadCount = 0;

    public static void reload() {
        reloadCount++;
    }
}
