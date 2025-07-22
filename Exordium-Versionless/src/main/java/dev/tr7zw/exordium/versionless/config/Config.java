package dev.tr7zw.exordium.versionless.config;

import lombok.Getter;
import lombok.Setter;

public class Config {

    public int configVersion = 3;
    public ComponentSettings globalSettings = new ComponentSettings(true, 30);

    @Getter
    @Setter
    public static class ComponentSettings {
        private boolean enabled = true;
        private int maxFps = 10;

        public ComponentSettings(boolean enabled, int maxFps) {
            this.enabled = enabled;
            this.maxFps = maxFps;
        }

        public ComponentSettings(boolean enabled, int maxFps, boolean forceBlend) {
            this.enabled = enabled;
            this.maxFps = maxFps;
        }
    }

}
