package dev.tr7zw.exordium.render;

import java.util.function.Supplier;

import dev.tr7zw.exordium.versionless.config.Config;
import dev.tr7zw.exordium.versionless.config.Config.ComponentSettings;

/**
 * Class to contain old Buffer render code, will be removed
 */
@Deprecated
public class LegacyBuffer extends BufferedComponent {

    public LegacyBuffer(Supplier<ComponentSettings> settings) {
        super(settings);
    }

    public LegacyBuffer(boolean forceBlending, Supplier<Config.ComponentSettings> settings) {
        super(forceBlending, settings);
    }

    @Deprecated
    public boolean render() {
        return render(this::shouldRenderNextCappedFrame);
    }

    protected boolean shouldForceRender() {
        return false;
    }

    @Deprecated
    public void renderEnd() {
        renderEnd(this::captureState);
    }

    @Deprecated
    public boolean shouldRenderNextCappedFrame() {
        throw new IllegalAccessError("Method not implemented");
    }

    /**
     * Take a snapshot of the current state of the component
     */
    @Deprecated
    public void captureState() {
        throw new IllegalAccessError("Method not implemented");
    }

}
