package dev.tr7zw.exordium;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.mojang.blaze3d.pipeline.RenderTarget;
import com.mojang.blaze3d.platform.GlStateManager;
import com.mojang.blaze3d.systems.RenderSystem;
import dev.tr7zw.exordium.config.Config;
import dev.tr7zw.exordium.config.ConfigUpgrader;
import dev.tr7zw.exordium.config.ExordiumConfigScreen;
import dev.tr7zw.exordium.util.CustomShaderManager;
import dev.tr7zw.exordium.util.DelayedRenderCallManager;
import net.minecraft.client.gui.screens.Screen;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;

public abstract class ExordiumModBase {

    public static final Logger LOGGER = LogManager.getLogger("Exordium");
    public static ExordiumModBase instance = new PreLoadedMod();
    private static boolean forceBlend;

    public Config config;
    private final File settingsFile = new File("config", "exordium.json");
    private final Gson gson = new GsonBuilder().setPrettyPrinting().create();
    private RenderTarget temporaryScreenOverwrite = null;
    private final DelayedRenderCallManager delayedRenderCallManager = new DelayedRenderCallManager();
    private final CustomShaderManager customShaderManager = new CustomShaderManager();
    private final BufferManager bufferManager = new BufferManager();

    public void onInitialize() {
        instance = this;
        if (settingsFile.exists()) {
            try {
                config = gson.fromJson(new String(Files.readAllBytes(settingsFile.toPath()), StandardCharsets.UTF_8),
                        Config.class);
            } catch (Exception ex) {
                System.out.println("Error while loading config! Creating a new one!");
                ex.printStackTrace();
            }
        }
        if (config == null) {
            config = new Config();
            writeConfig();
        } else {
            if (ConfigUpgrader.upgradeConfig(config)) {
                writeConfig(); // Config got modified
            }
        }
        bufferManager.initialize();
        initModloader();
    }

    public void writeConfig() {
        if (settingsFile.exists())
            settingsFile.delete();
        try {
            Files.write(settingsFile.toPath(), gson.toJson(config).getBytes(StandardCharsets.UTF_8));
        } catch (IOException e1) {
            e1.printStackTrace();
        }
    }

    public DelayedRenderCallManager getDelayedRenderCallManager() {
        return delayedRenderCallManager;
    }

    public abstract void initModloader();

    public Screen createConfigScreen(Screen parent) {
        return new ExordiumConfigScreen(parent);
    }

    public static boolean isForceBlend() {
        return forceBlend;
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

    public RenderTarget getTemporaryScreenOverwrite() {
        return temporaryScreenOverwrite;
    }

    public void setTemporaryScreenOverwrite(RenderTarget temporaryScreenOverwrite) {
        this.temporaryScreenOverwrite = temporaryScreenOverwrite;
    }

    public CustomShaderManager getCustomShaderManager() {
        return customShaderManager;
    }

    public BufferManager getBufferManager() {
        return bufferManager;
    }

}
