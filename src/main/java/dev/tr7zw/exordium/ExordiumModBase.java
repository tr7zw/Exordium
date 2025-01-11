package dev.tr7zw.exordium;

import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.mojang.blaze3d.pipeline.RenderTarget;
import com.mojang.blaze3d.platform.GlStateManager;
import com.mojang.blaze3d.systems.RenderSystem;

import dev.tr7zw.exordium.components.BufferManager;
import dev.tr7zw.exordium.config.ExordiumConfigScreen;
import dev.tr7zw.exordium.util.CustomShaderManager;
import dev.tr7zw.exordium.util.DelayedRenderCallManager;
import dev.tr7zw.exordium.versionless.config.Config;
import dev.tr7zw.exordium.versionless.config.ConfigUpgrader;
import lombok.Getter;
import lombok.Setter;
import net.minecraft.client.gui.screens.Screen;

public abstract class ExordiumModBase {

    public static final Logger LOGGER = LogManager.getLogger("Exordium");
    public static ExordiumModBase instance;
    @Getter
    private static boolean forceBlend;

    public Config config;
    private final File settingsFile = new File("config", "exordium.json");
    private final Gson gson = new GsonBuilder().setPrettyPrinting().create();
    @Setter
    @Getter
    private RenderTarget temporaryScreenOverwrite = null;
    @Getter
    private final DelayedRenderCallManager delayedRenderCallManager = new DelayedRenderCallManager();
    @Getter
    private final CustomShaderManager customShaderManager = new CustomShaderManager();
    private final BufferManager bufferManager = new BufferManager();
    private boolean lateInit = true;

    void onInitialize() {
        instance = this;
        if (settingsFile.exists()) {
            try {
                config = gson.fromJson(new String(Files.readAllBytes(settingsFile.toPath()), StandardCharsets.UTF_8),
                        Config.class);
            } catch (Exception ex) {
                LOGGER.error("Error while loading config! Creating a new one!", ex);
            }
        }
        if (config == null) {
            config = new Config();
            //#if MC >= 12104
            config.hotbarSettings.setForceUpdates(true);
            //#endif
            writeConfig();
        } else {
            if (ConfigUpgrader.upgradeConfig(config)) {
                writeConfig(); // Config got modified
            }
        }
        initModloader();
    }

    public void writeConfig() {
        if (settingsFile.exists())
            settingsFile.delete();
        try {
            Files.writeString(settingsFile.toPath(), gson.toJson(config));
        } catch (IOException e1) {
            e1.printStackTrace();
        }
    }

    public abstract void initModloader();

    Screen createConfigScreen(Screen parent) {
        return new ExordiumConfigScreen(parent);
    }

    public static void setForceBlend(boolean forceBlend) {
        ExordiumModBase.forceBlend = forceBlend;
    }

    public static void correctBlendMode() {
        RenderSystem.enableBlend();
        RenderSystem.blendFuncSeparate(GlStateManager.SourceFactor.SRC_ALPHA,
                GlStateManager.DestFactor.ONE_MINUS_SRC_ALPHA, GlStateManager.SourceFactor.ONE,
                GlStateManager.DestFactor.ONE_MINUS_SRC_ALPHA);
    }

    public BufferManager getBufferManager() {
        // FIXME
        if (lateInit) {
            bufferManager.initialize();
            lateInit = false;
        }
        return bufferManager;
    }

}
