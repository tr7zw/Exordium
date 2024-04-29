package dev.tr7zw.exordium.util;

public class ReloadTracker {
    public static int reloadCount = 0;

    private ReloadTracker() {

    }

    public static void reload() {
        reloadCount++;
    }
}
