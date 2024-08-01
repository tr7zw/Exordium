package dev.tr7zw.exordium.components;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Supplier;

import dev.tr7zw.exordium.ExordiumModBase;
import dev.tr7zw.exordium.access.ChatAccess;
import dev.tr7zw.exordium.access.GuiAccess;
import dev.tr7zw.exordium.access.TablistAccess;
import dev.tr7zw.exordium.access.VanillaBufferAccess.CrosshairOverlayAccess;
import dev.tr7zw.exordium.access.VanillaBufferAccess.DebugOverlayAccess;
import dev.tr7zw.exordium.access.VanillaBufferAccess.ExperienceBarOverlayAccess;
import dev.tr7zw.exordium.access.VanillaBufferAccess.HotbarOverlayAccess;
import dev.tr7zw.exordium.access.VanillaBufferAccess.ScoreBoardOverlayAccess;
import dev.tr7zw.exordium.access.VanillaBufferAccess.VignetteOverlayAccess;
import dev.tr7zw.exordium.components.support.XaerosMinimapComponent;
import dev.tr7zw.exordium.components.vanilla.ChatComponent;
import dev.tr7zw.exordium.util.BufferedComponent;
import dev.tr7zw.exordium.versionless.config.Config;
import lombok.NoArgsConstructor;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.scores.DisplaySlot;
import net.minecraft.world.scores.Scoreboard;

@NoArgsConstructor
public class BufferManager {

//    private final Map<ResourceLocation, Function<Gui, BufferedComponent>> vanillaBuffers = new HashMap<>();
//    private final Map<ResourceLocation, Consumer<HandlerData>> customHandlers = new HashMap<>();
//    private final Map<ResourceLocation, Runnable> customEndHandlers = new HashMap<>();
    private final Map<ResourceLocation, BufferInstance<?>> buffers = new HashMap<>();

    public void initialize() {
        ExordiumModBase inst = ExordiumModBase.instance;
        Minecraft minecraft = Minecraft.getInstance();
//        vanillaBuffers.put(new ResourceLocation("debug_text"),
//                gui -> ((DebugOverlayAccess) gui).getDebugOverlayBuffer());
//        vanillaBuffers.put(new ResourceLocation("crosshair"),
//                gui -> ((CrosshairOverlayAccess) gui).exordium_getCrosshairOverlayBuffer());
//        vanillaBuffers.put(new ResourceLocation("experience_bar"),
//                gui -> ((ExperienceBarOverlayAccess) gui).getExperienceBarOverlayBuffer());
//        vanillaBuffers.put(new ResourceLocation("scoreboard"),
//                gui -> ((ScoreBoardOverlayAccess) gui).getScoreBoardOverlayBuffer());
//        vanillaBuffers.put(new ResourceLocation("hotbar"), gui -> ((HotbarOverlayAccess) gui).getHotbarOverlayBuffer());
//        registerCustomHandler(new ResourceLocation("vignette"), data -> {
//            VignetteOverlayAccess vignette = (VignetteOverlayAccess) minecraft.gui;
//            if (ExordiumModBase.instance.config.vignetteSettings.isEnabled()) {
//                if (!vignette.getVignetteOverlayBuffer().render()) {
//                    vignette.renderCustomVignette(data.gui());
//                }
//                data.cancel().set(true);
//            }
//            vignette.getVignetteOverlayBuffer().renderEnd();
//        });
//        registerCustomHandler(new ResourceLocation("chat_panel"), data -> {
//            GuiAccess guiAccess = (GuiAccess) minecraft.gui;
//            ChatAccess chatAccess = (ChatAccess) guiAccess.getChatComponent();
//            chatAccess.updateState(guiAccess.getTickCount());
//            BufferedComponent bufferedComponent = chatAccess.getChatOverlayBuffer();
//            if (bufferedComponent.render()) {
//                data.cancel().set(true);
//            }
//        });
        registerBuffer(ChatComponent.getId(), new ChatComponent(), () -> inst.config.chatSettings);
        registerBuffer(XaerosMinimapComponent.getId(), new XaerosMinimapComponent(),
                () -> inst.config.xaerosMinimapSettings);

//        registerCustomEndHandler(new ResourceLocation("chat_panel"), () -> {
//            GuiAccess guiAccess = (GuiAccess) minecraft.gui;
//            ChatAccess chatAccess = (ChatAccess) guiAccess.getChatComponent();
//            BufferedComponent bufferedComponent = chatAccess.getChatOverlayBuffer();
//            bufferedComponent.renderEnd();
//        });
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
//
//    public BufferedComponent getBufferedComponent(ResourceLocation resourceLocation, Gui gui) {
//        Function<Gui, BufferedComponent> vanFun = vanillaBuffers.get(resourceLocation);
//        if (vanFun != null) {
//            return vanFun.apply(gui);
//        }
//        return null;
//    }
//
//    public Consumer<HandlerData> getCustomHandler(ResourceLocation resourceLocation) {
//        return customHandlers.get(resourceLocation);
//    }
//
//    public Runnable getCustomEndHandler(ResourceLocation resourceLocation) {
//        return customEndHandlers.get(resourceLocation);
//    }
//
//    public void registerCustomHandler(ResourceLocation resourceLocation, Consumer<HandlerData> handler) {
//        customHandlers.put(resourceLocation, handler);
//    }
//
//    public void registerCustomEndHandler(ResourceLocation resourceLocation, Runnable handler) {
//        customEndHandlers.put(resourceLocation, handler);
//    }
//
//    public record HandlerData(GuiGraphics gui, AtomicBoolean cancel) {
//    }

}
