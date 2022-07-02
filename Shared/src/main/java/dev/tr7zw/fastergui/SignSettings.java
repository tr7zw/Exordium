package dev.tr7zw.fastergui;

import dev.tr7zw.animatedfirstperson.config.debug.FloatSetting;

public class SignSettings {

    @FloatSetting(min = 100, max = 500, step = 1)
    public float bufferWidth = 301;
    @FloatSetting(min = 100, max = 500, step = 1)
    public float bufferHeight = 162;
    @FloatSetting(min = 100, max = 500, step = 1)
    public float renderWidth = 281;
    @FloatSetting(min = 100, max = 500, step = 1)
    public float renderHeight = 147;
    @FloatSetting(min = 10, max = 500, step = 1)
    public float ortoWidth = 200;
    @FloatSetting(min = -500, max = -10, step = 1)
    public float ortoHeight = -104;
    
    @FloatSetting(min = -100, max = 100, step = 0.1f)
    public float offsetX = -80.6f;
    @FloatSetting(min = -100, max = 100, step = 0.1f)
    public float offsetY = -21.1f;
    
    /*
    @FloatSetting(min = 100, max = 500, step = 1)
    public float bufferWidth = 260;
    @FloatSetting(min = 100, max = 500, step = 1)
    public float bufferHeight = 180;
    @FloatSetting(min = 100, max = 500, step = 1)
    public float renderWidth = 260;
    @FloatSetting(min = 100, max = 500, step = 1)
    public float renderHeight = 180;
    @FloatSetting(min = 10, max = 500, step = 1)
    public float ortoWidth = 100;
    @FloatSetting(min = -500, max = -10, step = 1)
    public float ortoHeight = -100;
     */
}
