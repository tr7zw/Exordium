package dev.tr7zw.exordium;

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
    }

}
