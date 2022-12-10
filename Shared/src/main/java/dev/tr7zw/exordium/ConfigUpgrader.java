package dev.tr7zw.exordium;

public class ConfigUpgrader {

    public static boolean upgradeConfig(Config config) {
        boolean changed = false;

        if(config.configVersion <= 1) {
            config.configVersion = 2;
            // set screenBuffering to false
            changed = true;
        }
        
        if(config.configVersion <= 2) {
            config.configVersion = 3;
            // remove screenBuffering
            changed = true;
        }
        
        // check for more changes here
        
        return changed;
    }
    
}
