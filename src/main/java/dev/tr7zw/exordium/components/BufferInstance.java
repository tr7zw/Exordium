package dev.tr7zw.exordium.components;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Supplier;

import dev.tr7zw.exordium.ExordiumModBase;
import dev.tr7zw.exordium.render.BufferedComponent;
import dev.tr7zw.exordium.util.PacingTracker;
import dev.tr7zw.exordium.util.ReloadListener;
import dev.tr7zw.exordium.versionless.config.Config;
import lombok.Getter;
import net.minecraft.resources.ResourceLocation;

public final class BufferInstance<T> {

    @Getter
    private final ResourceLocation id;
    private final BufferComponent<T> component;
    private final BufferedComponent buffer;
    private final PacingTracker pacing = new PacingTracker();
    private final Supplier<Config.ComponentSettings> settings;
    private final List<Supplier<Boolean>> updateListeners = new ArrayList<>();

    private boolean isCapturing = false;

    BufferInstance(ResourceLocation id, BufferComponent<T> component, Supplier<Config.ComponentSettings> settings) {
        this.id = id;
        this.component = component;
        this.buffer = new BufferedComponent(settings);
        this.settings = settings;
        registerUpdateListener(() -> settings.get().isForceUpdates());
        registerUpdateListener(new ReloadListener());
    }

    /**
     * Tries to render the buffer. Returns false if the buffer was not rendered and
     * the normal render logic should be used.
     * 
     * @param context
     * @return
     */
    public boolean renderBuffer(int ticks, T context) {
        if (!settings.get().isEnabled()) {
            // not enabled, skip
            return false;
        }
        if (buffer.needsBlendstateSample()) {
            // the intended blendstate is not know. Skip the buffer logic, let
            // it render normally, then grab the expected state
            return false;
        }

        boolean updateFrame = buffer.screenChanged() || (pacing.isCooldownOver() && hasUpdate(ticks, context));

        if (updateFrame) {
            // start capturing
            isCapturing = true;
            buffer.captureComponent();
            return false;
        }
        // we just render this buffered component
        buffer.renderBuffer();
        return true;
    }

    private boolean hasUpdate(int ticks, T context) {
        for (Supplier<Boolean> listener : updateListeners) {
            if (listener.get()) {
                return true;
            }
        }
        if (component.hasChanged(ticks, context)) {
            return true;
        }
        // nothing changed, so wait the poll rate for the next check
        pacing.setCooldown(System.currentTimeMillis() + (1000 / ExordiumModBase.instance.config.pollRate));
        return false;
    }

    /**
     * This method should be called after the render logic, no matter if the buffer
     * or normal render logic was used.
     * 
     * @param context
     */
    public void postRender(T context) {
        buffer.captureBlendstateSample();
        if (!isCapturing) {
            // we were not capturing, so nothing to do
            return;
        }
        isCapturing = false;
        component.captureState(context);
        pacing.setCooldown(System.currentTimeMillis() + (1000 / settings.get().getMaxFps()));
        buffer.finishCapture();
    }

    public void registerUpdateListener(Supplier<Boolean> hasChanged) {
        updateListeners.add(hasChanged);
    }

}
