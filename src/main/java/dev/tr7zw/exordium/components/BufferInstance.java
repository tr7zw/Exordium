package dev.tr7zw.exordium.components;

import java.util.function.Supplier;

import dev.tr7zw.exordium.ExordiumModBase;
import dev.tr7zw.exordium.util.PacingTracker;
import dev.tr7zw.exordium.versionless.config.Config;
import net.minecraft.client.Minecraft;

public final class BufferInstance {

    private final BufferedComponent buffer;
    private final PacingTracker pacing = new PacingTracker();
    private final Supplier<Config.ComponentSettings> settings;

    private boolean isCapturing = false;

    public BufferInstance(Supplier<Config.ComponentSettings> settings) {
        this.buffer = new BufferedComponent();
        this.settings = settings;
    }

    public boolean enabled() {
        return settings.get().isEnabled() && ExordiumModBase.instance.isInitialized();
    }

    /**
     * Tries to render the buffer. Returns false if the buffer was not rendered and
     * the normal render logic should be used.
     * 
     * @return
     */
    public boolean renderBuffer() {
        if (!enabled()) {
            // not enabled, skip
            return false;
        }
        boolean updateFrame = pacing.isCooldownOver();

        pacing.clearFlag();
        if (updateFrame) {
            // start capturing
            isCapturing = true;
            buffer.captureComponent();
            return false;
        }
        // we just render this buffered component
        buffer.getGuiTarget()
                .blitAndBlendToTexture(Minecraft.getInstance().getMainRenderTarget().getColorTextureView());
        return true;
    }

    public boolean skipGuiRendering() {
        return enabled() && !pacing.isCooldownOver();
    }

    /**
     * This method should be called after the render logic, no matter if the buffer
     * or normal render logic was used.
     * 
     */
    public void postRender() {
        if (!isCapturing) {
            // we were not capturing, so nothing to do
            return;
        }
        isCapturing = false;
        pacing.setCooldown(System.currentTimeMillis() + (1000 / settings.get().getMaxFps()));
        buffer.finishCapture();
        buffer.getGuiTarget()
                .blitAndBlendToTexture(Minecraft.getInstance().getMainRenderTarget().getColorTextureView());
    }

}
