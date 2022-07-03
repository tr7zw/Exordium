package dev.tr7zw.fastergui;

import dev.tr7zw.animatedfirstperson.config.debug.FloatSetting;

public class NametagSettings {

    @FloatSetting(min = 1, max = 20, step = 0.1f)
    public float bufferWidth = 4.9f;
    @FloatSetting(min = 1, max = 500, step = 1)
    public float bufferHeight = 100;
    @FloatSetting(min = 1, max = 500, step = 1)
    public float renderWidth = 309;
    @FloatSetting(min = 1, max = 500, step = 1)
    public float renderHeight = 310;
    @FloatSetting(min = -500, max = 500, step = 1)
    public float scaleSize = 154;
    
    @FloatSetting(min = -300, max = 0, step = 0.1f)
    public float offsetX = -154.1f;
    @FloatSetting(min = -300, max = 0, step = 0.1f)
    public float offsetY = -154.9f;

    
}
