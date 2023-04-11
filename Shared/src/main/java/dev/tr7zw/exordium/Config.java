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
    public ComponentSettings chatSettings = new ComponentSettings(true, 20);
    public ComponentSettings debugScreenSettings = new ComponentSettings(true, 10);
    public ComponentSettings hotbarSettings = new ComponentSettings(true, 20);
    public ComponentSettings experienceSettings = new ComponentSettings(true, 5);
    
    public class ComponentSettings {
        public boolean enabled = true;
        public int maxFps = 10;
        public ComponentSettings(boolean enabled, int maxFps) {
            super();
            this.enabled = enabled;
            this.maxFps = maxFps;
        }
    }
    
}
