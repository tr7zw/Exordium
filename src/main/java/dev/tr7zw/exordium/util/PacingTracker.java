package dev.tr7zw.exordium.util;

import lombok.Getter;
import lombok.Setter;

public class PacingTracker {

    @Getter
    @Setter
    private long cooldown = 0;
    private Boolean cooldownOver = null;

    public boolean isCooldownOver() {
        if (cooldownOver != null) {
            return cooldownOver;
        }
        cooldownOver = System.currentTimeMillis() > cooldown;
        return cooldownOver;
    }

    public void clearFlag() {
        cooldownOver = null;
    }

}
