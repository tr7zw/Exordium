package dev.tr7zw.fastergui;

import net.minecraftforge.fml.IExtensionPoint;
import net.minecraftforge.fml.ModLoadingContext;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;

@Mod("fastergui")
public class FaserGuiMod extends FasterGuiModBase {

    //Forge only
    private boolean onServer = false;
    
    public FaserGuiMod() {
        try {
            Class clientClass = net.minecraft.client.Minecraft.class;
        }catch(Throwable ex) {
            System.out.println("FasterGui Mod installed on a Server. Going to sleep.");
            onServer = true;
            return;
        }
        FMLJavaModLoadingContext.get().getModEventBus().addListener(this::setup);
    }

    private void setup(final FMLCommonSetupEvent event) {
        if(onServer)return;
        onInitialize();
    }


    @Override
    public void initModloader() {
        ModLoadingContext.get().registerExtensionPoint(IExtensionPoint.DisplayTest.class,
                () -> new IExtensionPoint.DisplayTest(
                        () -> ModLoadingContext.get().getActiveContainer().getModInfo().getVersion().toString(),
                        (remote, isServer) -> true));
    }

}
