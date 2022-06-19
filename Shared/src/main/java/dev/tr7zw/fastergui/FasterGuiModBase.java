package dev.tr7zw.fastergui;

import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.List;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import dev.tr7zw.config.CustomConfigScreen;
import net.minecraft.client.Option;
import dev.tr7zw.fastergui.util.BufferRenderer;
import net.minecraft.client.gui.screens.Screen;

public abstract class FasterGuiModBase {

    public static FasterGuiModBase instance;
    private static boolean forceBlend, blendBypass;

    public Config config;
    private final File settingsFile = new File("config", "fastergui.json");
    private final Gson gson = new GsonBuilder().setPrettyPrinting().create();
    private BufferRenderer screenBufferRenderer;

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
            if(ConfigUpgrader.upgradeConfig(config)) {
                writeConfig(); // Config got modified
            }
        }
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
    
    public BufferRenderer getScreenBufferRenderer() {
        if(screenBufferRenderer == null) {
            screenBufferRenderer = new BufferRenderer(true);
        }
        return screenBufferRenderer;
    }
    
    public abstract void initModloader();

    public Screen createConfigScreen(Screen parent) {
        CustomConfigScreen screen = new CustomConfigScreen(parent, "text.fastergui.title") {

            @Override
            public void initialize() {
                List<Option> options = new ArrayList<>();
                options.add(getOnOffOption("text.fastergui.enableGui", () -> config.enabledGui,
                        (b) -> config.enabledGui = b));
                options.add(getIntOption("text.fastergui.targetFramerateGui", 5, 60, () -> config.targetFPSIngameGui, (v) -> config.targetFPSIngameGui = v));
                options.add(getOnOffOption("text.fastergui.enableScreen", () -> config.enabledScreens,
                        (b) -> config.enabledScreens = b));
                options.add(getIntOption("text.fastergui.targetFramerateScreen", 20, 120, () -> config.targetFPSIngameScreens, (v) -> config.targetFPSIngameScreens = v));
              
                getOptions().addSmall(options.toArray(new Option[0]));

            }

            @Override
            public void save() {
                writeConfig();
            }

            @Override
            public void reset() {
                config = new Config();
                writeConfig();
            }

        };

        return screen;
    }

    public static boolean isForceBlend() {
        return forceBlend;
    }

    public static void setForceBlend(boolean forceBlend) {
        FasterGuiModBase.forceBlend = forceBlend;
    }

    public static boolean isBlendBypass() {
        return blendBypass;
    }

    public static void setBlendBypass(boolean blendBypass) {
        FasterGuiModBase.blendBypass = blendBypass;
    }
    
}
