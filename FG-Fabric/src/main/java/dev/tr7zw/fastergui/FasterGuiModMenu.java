package dev.tr7zw.fastergui;

import com.terraformersmc.modmenu.api.ConfigScreenFactory;
import com.terraformersmc.modmenu.api.ModMenuApi;

public class FasterGuiModMenu implements ModMenuApi {

    @Override
    public ConfigScreenFactory<?> getModConfigScreenFactory() {
        return parent -> {
            return FasterGuiModBase.instance.createConfigScreen(parent);
        };
    }  
    
}
