package dev.tr7zw.exordium.config;

import java.util.ArrayList;
import java.util.List;

import dev.tr7zw.config.CustomConfigScreen;
import dev.tr7zw.exordium.ExordiumModBase;
import dev.tr7zw.exordium.versionless.config.Config;
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

        options.add(getIntOption("text.exordium.pollRate", 20, 240, () -> config.pollRate, (v) -> config.pollRate = v));

        addSettings(options, config.chatSettings, "chat", false, false);
        addSettings(options, config.debugScreenSettings, "debug", false, true);
        addSettings(options, config.healthSettings, "health", false, false);
        addSettings(options, config.hotbarSettings, "hotbar", false, false);
        addSettings(options, config.experienceSettings, "experience", false, false);
        addSettings(options, config.scoreboardSettings, "scoreboard", false, false);
        addSettings(options, config.tablistSettings, "tablist", false, false);
        addSettings(options, config.vignetteSettings, "vignette", true, false);
        addSettings(options, config.crosshairSettings, "crosshair", true, false);
        addSettings(options, config.bossbarSettings, "bossbar", true, false);
        addSettings(options, config.xaerosMinimapSettings, "xaerosmimimap", true, true);

        getOptions().addSmall(options.toArray(new OptionInstance[0]));

    }

    private void addSettings(List<OptionInstance<?>> options, Config.ComponentSettings settings, String name,
            boolean hideBlending, boolean hideForceUpdates) {
        options.add(getOnOffOption("text.exordium.setting." + name + ".enabled", () -> settings.isEnabled(),
                (b) -> settings.setEnabled(b)));
        options.add(getIntOption("text.exordium.setting." + name + ".fps", 5, 60, () -> settings.getMaxFps(),
                (v) -> settings.setMaxFps(v)));
        if (!hideBlending) {
            options.add(getOnOffOption("text.exordium.setting." + name + ".forceblend", () -> settings.isForceBlend(),
                    (b) -> settings.setForceBlend(b)));
        }
        if (!hideForceUpdates) {
            options.add(getOnOffOption("text.exordium.setting." + name + ".forceupdates",
                    () -> settings.isForceUpdates(), (b) -> settings.setForceUpdates(b)));
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
