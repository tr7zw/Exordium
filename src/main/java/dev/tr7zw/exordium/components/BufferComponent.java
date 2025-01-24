package dev.tr7zw.exordium.components;

import dev.tr7zw.exordium.versionless.config.Config;

public interface BufferComponent<T> {

    public void captureState(T context);

    public boolean hasChanged(T context);

    public default boolean enabled(Config.ComponentSettings settings) {
        return settings.isEnabled();
    };

}
