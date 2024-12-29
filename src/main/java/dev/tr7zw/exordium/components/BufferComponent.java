package dev.tr7zw.exordium.components;

import dev.tr7zw.exordium.versionless.config.Config;
import net.minecraft.client.Minecraft;

public interface BufferComponent<T> {

    public void captureState(T context);

    public boolean hasChanged(int tickCount, T context);

    public default boolean enabled(Config.ComponentSettings settings) {
        if (Minecraft.getInstance().screen != null) {
            // during screens disable due to issues with the blur
            return false;
        }
        return settings.isEnabled();
    };

}
