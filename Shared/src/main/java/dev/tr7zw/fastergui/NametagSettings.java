package dev.tr7zw.fastergui;

import dev.tr7zw.animatedfirstperson.config.debug.FloatSetting;

public class NametagSettings {

    @FloatSetting(min = 1, max = 20, step = 0.1f)
    public float bufferWidth = 4.5f;
    @FloatSetting(min = 1, max = 500, step = 1)
    public float bufferHeight = 24;
    @FloatSetting(min = 1, max = 500, step = 1)
    public float renderHeight = 16;
    @FloatSetting(min = 0, max = 200, step = 1)
    public float scaleSize = 9;
    
    @FloatSetting(min = -1, max = 1, step = 0.01f)
    public float heightscale = -0.12f;

    
}
