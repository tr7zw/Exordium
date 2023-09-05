package dev.tr7zw.exordium;

import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

import dev.tr7zw.exordium.access.VanillaBufferAccess.DebugOverlayAccess;
import dev.tr7zw.exordium.util.BufferedComponent;
import net.minecraft.client.gui.Gui;
import net.minecraft.resources.ResourceLocation;

public class BufferManager {

    private Map<ResourceLocation, Function<Gui, BufferedComponent>> vanillaBuffers = new HashMap<>();

    public void initialize() {
        vanillaBuffers.put(new ResourceLocation("debug_text"),
                gui -> ((DebugOverlayAccess) gui).getDebugOverlayBuffer());
    }

    public BufferedComponent getBufferedComponent(ResourceLocation resourceLocation, Gui gui) {
        Function<Gui, BufferedComponent> vanFun = vanillaBuffers.get(resourceLocation);
        if (vanFun != null) {
            return vanFun.apply(gui);
        }
        return null;
    }

}
