package dev.tr7zw.exordium.components;

import java.util.HashMap;
import java.util.Map;
import java.util.function.Supplier;

import dev.tr7zw.exordium.ExordiumModBase;
import dev.tr7zw.exordium.components.vanilla.ChatComponent;
import dev.tr7zw.exordium.components.vanilla.CrosshairComponent;
import dev.tr7zw.exordium.components.vanilla.DebugOverlayComponent;
import dev.tr7zw.exordium.components.vanilla.VignetteComponent;
import dev.tr7zw.exordium.versionless.config.Config;
import lombok.NoArgsConstructor;
import net.minecraft.client.Minecraft;
import net.minecraft.resources.ResourceLocation;

@NoArgsConstructor
public class BufferManager {

    private final Map<ResourceLocation, BufferInstance<?>> buffers = new HashMap<>();

    public void initialize() {
        ExordiumModBase inst = ExordiumModBase.instance;
        Minecraft minecraft = Minecraft.getInstance();
        registerBuffer(DebugOverlayComponent.getId(), new DebugOverlayComponent(),
                () -> inst.config.debugScreenSettings);
        registerBuffer(CrosshairComponent.getId(), new CrosshairComponent(), () -> inst.config.crosshairSettings);
        registerBuffer(VignetteComponent.getId(), new VignetteComponent(), () -> inst.config.vignetteSettings);

//        vanillaBuffers.put(new ResourceLocation("experience_bar"),
//                gui -> ((ExperienceBarOverlayAccess) gui).getExperienceBarOverlayBuffer());
//        vanillaBuffers.put(new ResourceLocation("scoreboard"),
//                gui -> ((ScoreBoardOverlayAccess) gui).getScoreBoardOverlayBuffer());
//        vanillaBuffers.put(new ResourceLocation("hotbar"), gui -> ((HotbarOverlayAccess) gui).getHotbarOverlayBuffer());

        registerBuffer(ChatComponent.getId(), new ChatComponent(), () -> inst.config.chatSettings);

//        registerCustomHandler(new ResourceLocation("player_list"), data -> {
//            GuiAccess guiAccess = (GuiAccess) minecraft.gui;
//            TablistAccess tablistAccess = (TablistAccess) guiAccess.getPlayerTabOverlay();
//            Scoreboard scoreboard = minecraft.level.getScoreboard();
//            tablistAccess.updateState(scoreboard, scoreboard.getDisplayObjective(DisplaySlot.LIST));
//            BufferedComponent bufferedComponent = tablistAccess.getPlayerListOverlayBuffer();
//            if (bufferedComponent.render()) {
//                data.cancel().set(true);
//            }
//        });
//        registerCustomEndHandler(new ResourceLocation("player_list"), () -> {
//            GuiAccess guiAccess = (GuiAccess) minecraft.gui;
//            TablistAccess tabAccess = (TablistAccess) guiAccess.getPlayerTabOverlay();
//            BufferedComponent bufferedComponent = tabAccess.getPlayerListOverlayBuffer();
//            bufferedComponent.renderEnd();
//        });
    }

    @SuppressWarnings("unchecked")
    public <T> BufferInstance<T> getBufferInstance(ResourceLocation id, Class<T> context) {
        return (BufferInstance<T>) buffers.get(id);
    }

    public void registerBuffer(ResourceLocation id, BufferComponent<?> component,
            Supplier<Config.ComponentSettings> settings) {
        this.buffers.put(id, new BufferInstance<>(id, component, settings));
    }

}
