package dev.tr7zw.exordium.util.rendersystem;

import com.mojang.blaze3d.platform.GlStateManager;
import lombok.Getter;
import lombok.ToString;

@ToString
public class DepthStateHolder implements StateHolder {

    @Getter
    private boolean fetched = false;
    private int func = 1;
    private boolean mask = false;

    public void fetch() {
        fetched = true;
        func = GlStateManager.DEPTH.func;
        mask = GlStateManager.DEPTH.mask;
    }

    public void apply() {
        if (!fetched)
            return;
        GlStateManager._depthFunc(func);
        GlStateManager._depthMask(mask);
    }

}
