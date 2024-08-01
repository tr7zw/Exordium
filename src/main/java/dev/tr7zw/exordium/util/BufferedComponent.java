package dev.tr7zw.exordium.util;

import java.util.function.Supplier;

import org.joml.Vector3f;

import com.mojang.blaze3d.pipeline.RenderTarget;
import com.mojang.blaze3d.pipeline.TextureTarget;
import dev.tr7zw.exordium.ExordiumModBase;
import dev.tr7zw.exordium.versionless.config.Config;
import lombok.Getter;
import net.minecraft.client.Minecraft;

public class BufferedComponent {

    private static final Minecraft MINECRAFT = Minecraft.getInstance();
    @Getter
    private static Model model = null;
    private final Supplier<Config.ComponentSettings> settings;
    private final RenderTarget guiTarget = new TextureTarget(100, 100, true, false);
    private long cooldown = System.currentTimeMillis();
    private int guiScale = 0;
    private int reloadCount = 0;
    private boolean isRendering = false;
    private boolean forceBlending = false;
    private final BlendStateHolder blendStateHolder = new BlendStateHolder();

    public BufferedComponent(Supplier<Config.ComponentSettings> settings) {
        this(false, settings);
    }

    public BufferedComponent(boolean forceBlending, Supplier<Config.ComponentSettings> settings) {
        this.forceBlending = forceBlending;
        this.settings = settings;
    }

    private static void refreshModel(int screenWidth, int screenHeight) {
        if (model != null) {
            model.close();
        }

        Vector3f[] modelData = new Vector3f[] { new Vector3f(0.0f, screenHeight, -90.0f),
                new Vector3f(screenWidth, screenHeight, -90.0F), new Vector3f(screenWidth, 0.0F, -90.0F),
                new Vector3f(0.0F, 0.0F, -90.0F), };
        Model.Vector2f[] uvData = new Model.Vector2f[] { new Model.Vector2f(0.0f, 0.0f), new Model.Vector2f(1.0f, 0.0f),
                new Model.Vector2f(1.0f, 1.0f), new Model.Vector2f(0.0f, 1.0f), };
        model = new Model(modelData, uvData);
    }

    @Deprecated
    public boolean render() {
        return render(this::shouldRenderNextCappedFrame);
    }

    /**
     * @return true if the buffer was used. False = render as usual
     */
    public boolean render(Supplier<Boolean> hasChanged) {
        if (!settings.get().isEnabled()) {
            return false;
        }
        if (!blendStateHolder.isBlendStateFetched()) { // the intended blendstate is not know. Skip the buffer logic,
                                                       // let
                                                       // it render normally, then grab the expected state
            return false;
        }
        int screenWidth = MINECRAFT.getWindow().getGuiScaledWidth();
        int screenHeight = MINECRAFT.getWindow().getGuiScaledHeight();
        boolean forceRender = false;
        if (guiTarget.width != MINECRAFT.getWindow().getWidth() || guiTarget.height != MINECRAFT.getWindow().getHeight()
                || MINECRAFT.options.guiScale().get() != guiScale) {
            guiTarget.resize(MINECRAFT.getWindow().getWidth(), MINECRAFT.getWindow().getHeight(), true);
            refreshModel(screenWidth, screenHeight);
            guiScale = MINECRAFT.options.guiScale().get();
            forceRender = true;
        }
        if (model == null) {
            refreshModel(screenWidth, screenHeight);
        }
        boolean updateFrame = this.shouldForceRender() || forceRender || (System.currentTimeMillis() > cooldown
                && (settings.get().isForceUpdates() || needsRenderPaced(hasChanged)));
        if (!updateFrame) {
            // renderTextureOverlay(guiTarget.getColorTextureId());
            ExordiumModBase.instance.getDelayedRenderCallManager().addBufferedComponent(this);
            blendStateHolder.apply();
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

    protected boolean shouldForceRender() {
        return false;
    }

    @Deprecated
    public void renderEnd() {
        renderEnd(this::captureState);
    }

    public void renderEnd(Runnable capture) {
        if (!blendStateHolder.isBlendStateFetched()) {
            // capture the expected blend state
            blendStateHolder.fetch();
        }
        if (!isRendering) {
            return;
        }
        capture.run(); // take the current state of the component
        ExordiumModBase.instance.setTemporaryScreenOverwrite(null);
        guiTarget.unbindWrite();
        Minecraft.getInstance().getMainRenderTarget().bindWrite(true);
        cooldown = System.currentTimeMillis() + (1000 / settings.get().getMaxFps());
        isRendering = false;
        if (forceBlending || settings.get().isForceBlend()) {
            ExordiumModBase.setForceBlend(false);
        }
        // renderTextureOverlay(guiTarget.getColorTextureId());
        ExordiumModBase.instance.getDelayedRenderCallManager().addBufferedComponent(this);
        blendStateHolder.apply();
    }

    public int getTextureId() {
        return guiTarget.getColorTextureId();
    }

    public boolean isRendering() {
        return isRendering;
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
        cooldown = System.currentTimeMillis() + (1000 / ExordiumModBase.instance.config.pollRate);
        return false;
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
