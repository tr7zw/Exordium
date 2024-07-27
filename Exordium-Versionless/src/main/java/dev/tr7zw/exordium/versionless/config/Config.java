package dev.tr7zw.exordium.versionless.config;

public class Config {

    public int configVersion = 2;
    public int pollRate = 60;
    public ComponentSettings chatSettings = new ComponentSettings(true, 20);
    public ComponentSettings debugScreenSettings = new ComponentSettings(true, 10);
    public ComponentSettings hotbarSettings = new ComponentSettings(true, 20);
    public ComponentSettings experienceSettings = new ComponentSettings(true, 5);
    public ComponentSettings healthSettings = new ComponentSettings(true, 20);
    public ComponentSettings scoreboardSettings = new ComponentSettings(true, 5);
    public ComponentSettings tablistSettings = new ComponentSettings(true, 20);
    public ComponentSettings vignetteSettings = new ComponentSettings(true, 5);
    public ComponentSettings crosshairSettings = new ComponentSettings(false, 20);
    public ComponentSettings bossbarSettings = new ComponentSettings(true, 5);

    public static class ComponentSettings {
        public boolean enabled = true;
        public int maxFps = 10;
        public boolean forceBlend = false;
        public boolean forceUpdates = false;

        public ComponentSettings(boolean enabled, int maxFps) {
            this.enabled = enabled;
            this.maxFps = maxFps;
        }
    }

}
