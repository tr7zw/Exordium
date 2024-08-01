package dev.tr7zw.exordium.versionless.config;

import lombok.Getter;
import lombok.Setter;

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
    public ComponentSettings xaerosMinimapSettings = new ComponentSettings(true, 30);

    @Getter
    @Setter
    public static class ComponentSettings {
        private boolean enabled = true;
        private int maxFps = 10;
        private boolean forceBlend = false;
        private boolean forceUpdates = false;

        public ComponentSettings(boolean enabled, int maxFps) {
            this.enabled = enabled;
            this.maxFps = maxFps;
        }
    }

}
