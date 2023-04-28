package dev.tr7zw.exordium;

import com.llamalad7.mixinextras.MixinExtrasBootstrap;

import net.fabricmc.api.ClientModInitializer;

public class ExordiumMod extends ExordiumModBase implements ClientModInitializer {
    
    @Override
    public void onInitializeClient() {
        super.onInitialize();
        MixinExtrasBootstrap.init();
    }

    @Override
    public void initModloader() {

    }

}
