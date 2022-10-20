package dev.tr7zw.exordium;

public class ConfigUpgrader {

    public static boolean upgradeConfig(Config config) {
        boolean changed = false;

        if(config.configVersion <= 1) {
            config.enabledScreens = false;
            config.configVersion = 2;
            changed = true;
        }
        
        // check for more changes here
        
        return changed;
    }
    
}
