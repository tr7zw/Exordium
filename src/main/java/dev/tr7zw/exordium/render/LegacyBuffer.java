package dev.tr7zw.exordium.render;

import java.util.function.Supplier;

import dev.tr7zw.exordium.ExordiumModBase;
import dev.tr7zw.exordium.util.PacingTracker;
import dev.tr7zw.exordium.util.ReloadTracker;
import dev.tr7zw.exordium.versionless.config.Config;
import dev.tr7zw.exordium.versionless.config.Config.ComponentSettings;
import net.minecraft.client.Minecraft;

/**
 * Class to contain old Buffer render code, will be removed
 */
@Deprecated
public class LegacyBuffer extends BufferedComponent {

    private static final Minecraft MINECRAFT = Minecraft.getInstance();
    private int reloadCount = 0;
    private PacingTracker pacing = new PacingTracker();
    private boolean isRendering = false;

    public LegacyBuffer(Supplier<ComponentSettings> settings) {
        super(settings);
    }

    public LegacyBuffer(boolean forceBlending, Supplier<Config.ComponentSettings> settings) {
        super(forceBlending, settings);
    }

    public boolean render(Supplier<Boolean> hasChanged) {
        if (!settings.get().isEnabled()) {
            return false;
        }
        if (!blendStateHolder.isBlendStateFetched()) { // the intended blendstate is not know. Skip the buffer logic,
                                                       // let
                                                       // it render normally, then grab the expected state
            return false;
        }
        boolean forceRender = false;
        // Check for Screen size/scaling changes
        if (screenTracker.hasChanged()) {
            screenTracker.updateState();
            refreshModel(MINECRAFT.getWindow().getGuiScaledWidth(), MINECRAFT.getWindow().getGuiScaledHeight());
            forceRender = true;
        }
        //
        if (model == null) {
            refreshModel(MINECRAFT.getWindow().getGuiScaledWidth(), MINECRAFT.getWindow().getGuiScaledHeight());
        }
        boolean updateFrame = forceRender
                || (pacing.isCooldownOver() && (settings.get().isForceUpdates() || needsRenderPaced(hasChanged)));

        if (!updateFrame) {
            // we just render this component
            renderBuffer();
            return true;
        }
        guiTarget.setClearColor(0, 0, 0, 0);
        guiTarget.clear(false);
        guiTarget.bindWrite(false);
        isRendering = true;
        ExordiumModBase.instance.setTemporaryScreenOverwrite(guiTarget);

        ExordiumModBase.correctBlendMode();
        if (forceBlending || settings.get().isForceBlend()) {
            ExordiumModBase.setForceBlend(true);
        }
        guiTarget.bindWrite(false);
        return false;
    }

    public void renderEnd(Runnable capture) {
        if (!blendStateHolder.isBlendStateFetched()) {
            // capture the expected blend state
            blendStateHolder.fetch();
        }
        if (!isRendering) {
            // the buffer was used, nothing to do
            return;
        }
        capture.run(); // capture the current state of the component as the current rendered state
        ExordiumModBase.instance.setTemporaryScreenOverwrite(null);
        guiTarget.unbindWrite();
        Minecraft.getInstance().getMainRenderTarget().bindWrite(true);
        pacing.setCooldown(System.currentTimeMillis() + (1000 / settings.get().getMaxFps()));
        isRendering = false;
        if (forceBlending || settings.get().isForceBlend()) {
            ExordiumModBase.setForceBlend(false);
        }
        renderBuffer();
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

    /**
     * Checks for changes
     * 
     * @return
     */
    private boolean needsRenderPaced(Supplier<Boolean> hasChanged) {
        boolean reloadOccurred = false;
        if (reloadCount != ReloadTracker.getReloadCount()) {
            reloadCount = ReloadTracker.getReloadCount();
            reloadOccurred = true;
        }

        if (reloadOccurred || hasChanged.get()) {
            return true;
        }
        pacing.setCooldown(System.currentTimeMillis() + (1000 / ExordiumModBase.instance.config.pollRate));
        return false;
    }

}
