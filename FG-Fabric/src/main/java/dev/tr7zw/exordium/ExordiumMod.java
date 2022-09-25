package dev.tr7zw.exordium;

import dev.tr7zw.animatedfirstperson.config.debug.DebugScreen;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.keybinding.v1.KeyBindingHelper;
import net.minecraft.client.KeyMapping;
import net.minecraft.client.Minecraft;

public class ExordiumMod extends ExordiumModBase implements ClientModInitializer {
    
    @Override
    public void onInitializeClient() {
        super.onInitialize();
        
    }

    @Override
    public void initModloader() {
//        KeyBindingHelper.registerKeyBinding(keybind);
//        ClientTickEvents.START_CLIENT_TICK.register(e ->
//        {
//            if(keybind.isDown()) {
//                Minecraft.getInstance().setScreen(DebugScreen.createDebugGui(null, () -> FasterGuiModBase.nametagSettings));
//            }
//        });
    }

}
