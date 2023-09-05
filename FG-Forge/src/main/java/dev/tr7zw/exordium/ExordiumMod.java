package dev.tr7zw.exordium;

import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.client.event.RenderGuiEvent;
import net.minecraftforge.client.event.RenderGuiOverlayEvent;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.eventbus.api.Event.Result;
import net.minecraftforge.fml.IExtensionPoint;
import net.minecraftforge.fml.ModLoadingContext;
import net.minecraftforge.fml.common.Mod;

@Mod("exordium")
public class ExordiumMod extends ExordiumModBase {

    //Forge only
    private boolean onServer = false;
    
    public ExordiumMod() {
        try {
            Class clientClass = net.minecraft.client.Minecraft.class;
        }catch(Throwable ex) {
            System.out.println("Exordium Mod installed on a Server. Going to sleep.");
            onServer = true;
            return;
        }
        if(onServer)return;
        LOGGER.info("Loading Exordium!");
        super.onInitialize();
    }

    @Override
    public void initModloader() {
        ModLoadingContext.get().registerExtensionPoint(IExtensionPoint.DisplayTest.class,
                () -> new IExtensionPoint.DisplayTest(
                        () -> ModLoadingContext.get().getActiveContainer().getModInfo().getVersion().toString(),
                        (remote, isServer) -> true));
        MinecraftForge.EVENT_BUS.addListener(this::preOverlayRender);
        MinecraftForge.EVENT_BUS.addListener(this::postOverlayRender);
        MinecraftForge.EVENT_BUS.addListener(this::postRenderGuiEvent);
    }
    
    private void postRenderGuiEvent(RenderGuiEvent.Post event) {
        ExordiumModBase.instance.getDelayedRenderCallManager().renderComponents();
    }
    
    private void preOverlayRender(RenderGuiOverlayEvent.Pre event) {
        if(!event.isCanceled()) {
            if(event.getOverlay().id().equals(new ResourceLocation("minecraft", "debug_text"))) {
                if (getBufferManager().getDebugBuffer().render()) {
                    event.setCanceled(true);
                }
            }
        }
    }
    
    private void postOverlayRender(RenderGuiOverlayEvent.Post event) {
        if(!event.isCanceled()) {
            if(event.getOverlay().id().equals(new ResourceLocation("minecraft", "debug_text"))) {
                getBufferManager().getDebugBuffer().renderEnd();
            }
        }
    }

}
