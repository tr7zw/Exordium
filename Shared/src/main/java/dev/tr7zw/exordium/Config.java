package dev.tr7zw.exordium;

public class Config {

    public int configVersion = 2;
    public boolean enabledGui = true;
    public int targetFPSIngameGui = 20;
    public boolean enabledGuiAnimationSpeedup = true;
    public int targetFPSIngameGuiAnimated = 60;
    public boolean enabledScreensLegacy = false;
    public int targetFPSIngameScreens = 60;
    public boolean enableSignBuffering = true;
    public int targetFPSNameTags = 60;
    public boolean enableNametagScreenBuffering = false;
    public ComponentSettings chatSettings = new ComponentSettings();
    
    public class ComponentSettings {
        public boolean enabled = true;
        public int maxFps = 20;
    }
    
}
