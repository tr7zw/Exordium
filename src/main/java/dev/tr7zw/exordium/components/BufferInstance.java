package dev.tr7zw.exordium.components;

import java.util.function.Supplier;

import dev.tr7zw.exordium.util.BufferedComponent;
import dev.tr7zw.exordium.versionless.config.Config;
import lombok.Getter;
import net.minecraft.resources.ResourceLocation;

public final class BufferInstance<T> {

    @Getter
    private final ResourceLocation id;
    private final BufferComponent<T> component;
    private final BufferedComponent buffer;

    BufferInstance(ResourceLocation id, BufferComponent<T> component, Supplier<Config.ComponentSettings> settings) {
        this.id = id;
        this.component = component;
        this.buffer = new BufferedComponent(settings);
    }

    /**
     * Tries to render the buffer. Returns false if the buffer was not rendered and
     * the normal render logic should be used.
     * 
     * @param context
     * @return
     */
    public boolean renderBuffer(int ticks, T context) {
        return buffer.render(() -> component.hasChanged(ticks, context));
    }

    /**
     * This method should be called after the render logic, no matter if the buffer
     * or normal render logic was used.
     * 
     * @param context
     */
    public void postRender(T context) {
        buffer.renderEnd(() -> component.captureState(context));
    }

}
