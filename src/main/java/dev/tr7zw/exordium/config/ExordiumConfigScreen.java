package dev.tr7zw.exordium.config;

import java.util.ArrayList;
import java.util.List;

import dev.tr7zw.exordium.ExordiumModBase;
import dev.tr7zw.exordium.versionless.config.Config;
import dev.tr7zw.transition.mc.ComponentProvider;
import dev.tr7zw.trender.gui.client.AbstractConfigScreen;
import dev.tr7zw.trender.gui.client.BackgroundPainter;
import dev.tr7zw.trender.gui.widget.WButton;
import dev.tr7zw.trender.gui.widget.WGridPanel;
import dev.tr7zw.trender.gui.widget.WTabPanel;
import dev.tr7zw.trender.gui.widget.data.Insets;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.CommonComponents;

public class ExordiumConfigScreen extends AbstractConfigScreen {

    public ExordiumConfigScreen(Screen lastScreen) {
        super(ComponentProvider.translatable("text.exordium.title"), lastScreen);
        
        List<OptionInstance> options = new ArrayList<>();

        addSettings(options, ExordiumModBase.instance.config.globalSettings, "global");

        WGridPanel root = new WGridPanel(8);
        root.setBackgroundPainter(BackgroundPainter.VANILLA);
        root.setInsets(Insets.ROOT_PANEL);
        setRootPanel(root);

        WTabPanel wTabPanel = new WTabPanel();

        var generalOptionList = createOptionList(options);
        generalOptionList.setGap(-1);
        generalOptionList.setSize(14 * 20, 9 * 20);
        wTabPanel.add(generalOptionList,
                b -> b.title(ComponentProvider.translatable("text.paperdoll.tab.general_options")));

        wTabPanel.layout();
        root.add(wTabPanel, 0, 2);

        WButton doneButton = new WButton(CommonComponents.GUI_DONE);
        doneButton.setOnClick(() -> {
            save();
            Minecraft.getInstance().setScreen(lastScreen);
        });
        root.add(doneButton, 0, 27, 6, 2);

        WButton resetButton = new WButton(ComponentProvider.translatable("controls.reset"));
        resetButton.setOnClick(() -> {
            reset();
            root.layout();
        });
        root.add(resetButton, 37, 27, 6, 2);

        root.setBackgroundPainter(BackgroundPainter.VANILLA);

        root.validate(this);
        root.setHost(this);
    }

    private void addSettings(List<OptionInstance> options, Config.ComponentSettings settings, String name) {
        options.add(getOnOffOption("text.exordium.setting." + name + ".enabled", () -> settings.isEnabled(),
                (b) -> settings.setEnabled(b)));
        options.add(getIntOption("text.exordium.setting." + name + ".fps", 5, 60, () -> settings.getMaxFps(),
                (v) -> settings.setMaxFps(v)));
    }

    @Override
    public void save() {
        ExordiumModBase.instance.writeConfig();
    }

    @Override
    public void reset() {
        ExordiumModBase.instance.config = new Config();
        ExordiumModBase.instance.writeConfig();
    }

}
