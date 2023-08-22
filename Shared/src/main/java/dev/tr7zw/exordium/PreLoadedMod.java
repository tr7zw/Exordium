package dev.tr7zw.exordium;

import dev.tr7zw.exordium.config.Config;

/**
 * Dirty workaround cause Forge calls ui render methods of mixins before the mod is loaded?!?
 * 
 * @author tr7zw
 *
 */
public class PreLoadedMod extends ExordiumModBase {

    public PreLoadedMod() {
        this.config = new Config();
    }
    
    @Override
    public void initModloader() {

    }

}
