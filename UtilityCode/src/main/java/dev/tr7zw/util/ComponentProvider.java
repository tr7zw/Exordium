package dev.tr7zw.util;

import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;

/**
 * Utility class to get Components, for better bridging between 1.19+ and pre 1.19
 * 
 * @author tr7zw
 *
 */
public class ComponentProvider {

    public static MutableComponent literal(String string) {
        return Component.literal(string);
    }

    public static MutableComponent translatable(String string) {
        return Component.translatable(string);
    }

    public static MutableComponent translatable(String string, Object... objects) {
        return Component.translatable(string, objects);
    }

    public static MutableComponent empty() {
        return Component.empty();
    }

}