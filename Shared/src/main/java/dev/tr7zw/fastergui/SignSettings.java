package dev.tr7zw.fastergui;

import dev.tr7zw.animatedfirstperson.config.debug.FloatSetting;

public class SignSettings {

    @FloatSetting(min = 100, max = 500, step = 1)
    public float bufferWidth = 366;
    @FloatSetting(min = 100, max = 500, step = 1)
    public float bufferHeight = 366;
    @FloatSetting(min = 100, max = 500, step = 1)
    public float renderWidth = 183;
    @FloatSetting(min = 100, max = 500, step = 1)
    public float renderHeight = 183;
    @FloatSetting(min = -500, max = 500, step = 1)
    public float scaleSize = 91;
    
    @FloatSetting(min = -100, max = 100, step = 0.1f)
    public float offsetX = -63.1f;
    @FloatSetting(min = -100, max = 100, step = 0.1f)
    public float offsetY = -91.7f;

}
