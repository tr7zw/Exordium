package dev.tr7zw.exordium;

import java.util.concurrent.atomic.AtomicBoolean;
import java.util.function.Consumer;

import dev.tr7zw.exordium.BufferManager.HandlerData;
import dev.tr7zw.exordium.util.BufferedComponent;
import net.minecraft.client.Minecraft;
import net.minecraftforge.client.ConfigScreenHandler.ConfigScreenFactory;
import net.minecraftforge.client.event.RenderGuiEvent;
import net.minecraftforge.client.event.RenderGuiOverlayEvent;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.IExtensionPoint;
import net.minecraftforge.fml.ModLoadingContext;
import net.minecraftforge.fml.common.Mod;

@Mod("exordium")
public class ExordiumMod extends ExordiumModBase {

    // Forge only
    private boolean onServer = false;

    private final Minecraft minecraft = Minecraft.getInstance();

    public ExordiumMod() {
        try {
            Class clientClass = net.minecraft.client.Minecraft.class;
        } catch (Throwable ex) {
            System.out.println("Exordium Mod installed on a Server. Going to sleep.");
            onServer = true;
            return;
        }
        if (onServer)
            return;
        LOGGER.info("Loading Exordium!");
        super.onInitialize();
    }

    @Override
    public void initModloader() {
        ModLoadingContext.get().registerExtensionPoint(IExtensionPoint.DisplayTest.class,
                () -> new IExtensionPoint.DisplayTest(
                        () -> ModLoadingContext.get().getActiveContainer().getModInfo().getVersion().toString(),
                        (remote, isServer) -> true));
        ModLoadingContext.get().registerExtensionPoint(ConfigScreenFactory.class,
                () -> new ConfigScreenFactory((mc, screen) -> {
                    return createConfigScreen(screen);
                }));
        MinecraftForge.EVENT_BUS.addListener(this::preOverlayRender);
        MinecraftForge.EVENT_BUS.addListener(this::postOverlayRender);
        MinecraftForge.EVENT_BUS.addListener(this::postRenderGuiEvent);
    }

    private void postRenderGuiEvent(RenderGuiEvent.Post event) {
        ExordiumModBase.instance.getDelayedRenderCallManager().renderComponents();
    }

    private void preOverlayRender(RenderGuiOverlayEvent.Pre event) {
        if (!event.isCanceled()) {
            BufferedComponent comp = getBufferManager().getBufferedComponent(event.getOverlay().id(), minecraft.gui);
            if (comp != null) {
                if (comp.render()) {
                    event.setCanceled(true);
                }
            } else {
                Consumer<HandlerData> handler = getBufferManager().getCustomHandler(event.getOverlay().id());
                if (handler != null) {
                    HandlerData data = new HandlerData(event.getGuiGraphics(), new AtomicBoolean());
                    handler.accept(data);
                    event.setCanceled(data.cancel().get());
                }
            }
        }
    }

    private void postOverlayRender(RenderGuiOverlayEvent.Post event) {
        if (!event.isCanceled()) {
            BufferedComponent comp = getBufferManager().getBufferedComponent(event.getOverlay().id(), minecraft.gui);
            if (comp != null) {
                comp.renderEnd();
            } else {
                Runnable handler = getBufferManager().getCustomEndHandler(event.getOverlay().id());
                if (handler != null) {
                    handler.run();
                }
            }
        }
    }

}
