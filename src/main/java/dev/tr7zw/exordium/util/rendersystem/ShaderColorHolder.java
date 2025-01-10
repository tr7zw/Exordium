package dev.tr7zw.exordium.util.rendersystem;

import com.mojang.blaze3d.systems.RenderSystem;

import lombok.Getter;
import lombok.ToString;

@ToString
public class ShaderColorHolder implements StateHolder {

    @Getter
    private boolean fetched = false;
    private float[] func = new float[4];

    public void fetch() {
        fetched = true;
        float[] cur = RenderSystem.getShaderColor();
        func[0] = cur[0];
        func[1] = cur[1];
        func[2] = cur[2];
        func[3] = cur[3];
    }

    public void apply() {
        if (!fetched)
            return;
        RenderSystem.setShaderColor(func[0], func[1], func[2], func[3]);
    }

}
