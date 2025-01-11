package dev.tr7zw.exordium.util.rendersystem;

import com.mojang.blaze3d.platform.GlStateManager;
import lombok.Getter;
import lombok.ToString;

@ToString
public class BlendStateHolder implements StateHolder {

    @Getter
    private boolean fetched = false;
    private boolean enabled = false;
    private int srcRgb = 1;
    private int dstRgb = 0;
    private int srcAlpha = 1;
    private int dstAlpha = 0;

    public void fetch() {
        fetched = true;
        enabled = GlStateManager.BLEND.mode.enabled;
        srcRgb = GlStateManager.BLEND.srcRgb;
        srcAlpha = GlStateManager.BLEND.srcAlpha;
        dstRgb = GlStateManager.BLEND.dstRgb;
        dstAlpha = GlStateManager.BLEND.dstAlpha;
    }

    public void apply() {
        if (!fetched)
            return;
        GlStateManager._blendFuncSeparate(srcRgb, dstRgb, srcAlpha, dstAlpha);
        GlStateManager.BLEND.mode.setEnabled(enabled);
    }

}
