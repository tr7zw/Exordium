package dev.tr7zw.exordium.util;

import com.mojang.blaze3d.platform.GlStateManager;

public class BlendSateHolder {

    private boolean blendStateFetched = false;
    private int srcRgb = 1;
    private int dstRgb = 0;
    private int srcAlpha = 1;
    private int dstAlpha = 0;
    
    public boolean isBlendStateFetched() {
        return blendStateFetched;
    }
 
    public void fetch() {
        blendStateFetched = true;
        srcRgb = GlStateManager.BLEND.srcRgb;
        srcAlpha = GlStateManager.BLEND.srcAlpha;
        dstRgb = GlStateManager.BLEND.dstRgb;
        dstAlpha = GlStateManager.BLEND.dstAlpha;
    }
    
    public void apply() {
        GlStateManager._blendFuncSeparate(srcRgb, dstRgb, srcAlpha, dstAlpha);
    }
    
}
