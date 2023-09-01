package dev.tr7zw.exordium.config;

import java.util.ArrayList;
import java.util.List;

import dev.tr7zw.config.CustomConfigScreen;
import dev.tr7zw.exordium.ExordiumModBase;
import dev.tr7zw.exordium.config.Config.ComponentSettings;
import net.minecraft.client.OptionInstance;
import net.minecraft.client.gui.screens.Screen;

public class ExordiumConfigScreen extends CustomConfigScreen {

    public ExordiumConfigScreen(Screen lastScreen) {
        super(lastScreen, "text.exordium.title");
    }

    @Override
    public void initialize() {
        Config config = ExordiumModBase.instance.config;
        List<OptionInstance<?>> options = new ArrayList<>();

        options.add(getOnOffOption("text.exordium.enableSignBuffering", () -> config.enableSignBuffering,
                (b) -> config.enableSignBuffering = b));

        options.add(
                getOnOffOption("text.exordium.enableNametagScreenBuffering", () -> config.enableNametagScreenBuffering,
                        (b) -> config.enableNametagScreenBuffering = b));
        options.add(getIntOption("text.exordium.targetFPSNameTags", 30, 80, () -> config.targetFPSNameTags,
                (v) -> config.targetFPSNameTags = v));
        options.add(getIntOption("text.exordium.pollRate", 20, 240, () -> config.pollRate, (v) -> config.pollRate = v));

        addSettings(options, config.chatSettings, "chat", false);
        addSettings(options, config.debugScreenSettings, "debug", false);
        addSettings(options, config.healthSettings, "health", false);
        addSettings(options, config.hotbarSettings, "hotbar", false);
        addSettings(options, config.experienceSettings, "experience", false);
        addSettings(options, config.scoreboardSettings, "scoreboard", false);
        addSettings(options, config.tablistSettings, "tablist", false);
        addSettings(options, config.vignetteSettings, "vignette", true);
        addSettings(options, config.crosshairSettings, "crosshair", true);

        getOptions().addSmall(options.toArray(new OptionInstance[0]));

    }

    private void addSettings(List<OptionInstance<?>> options, ComponentSettings settings, String name, boolean hideBlending) {
        options.add(getOnOffOption("text.exordium.setting." + name + ".enabled", () -> settings.enabled,
                (b) -> settings.enabled = b));
        options.add(getIntOption("text.exordium.setting." + name + ".fps", 5, 60, () -> settings.maxFps,
                (v) -> settings.maxFps = v));
        if (!hideBlending) {
            options.add(getOnOffOption("text.exordium.setting." + name + ".forceblend", () -> settings.forceBlend,
                    (b) -> settings.forceBlend = b));
        }
        if (!name.equals("debug")) { // debug has that already built in and on
            options.add(getOnOffOption("text.exordium.setting." + name + ".forceupdates", () -> settings.forceUpdates,
                    (b) -> settings.forceUpdates = b));
        }
    }

    @Override
    public void save() {
        ExordiumModBase.instance.writeConfig();
    }

    @Override
    public void reset() {
        ExordiumModBase.instance.config = new Config();
        ExordiumModBase.instance.writeConfig();
        this.rebuildWidgets();
    }

}
