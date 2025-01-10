package dev.tr7zw.exordium.util.rendersystem;

import com.mojang.blaze3d.systems.RenderSystem;

import lombok.Getter;
import lombok.ToString;

@ToString
public class ShaderTextureHolder implements StateHolder {

    @Getter
    private boolean fetched = false;
    private int[] texture = new int[12];

    public void fetch() {
        fetched = true;
        for (int i = 0; i < 12; i++) {
            texture[i] = RenderSystem.getShaderTexture(i);
        }
    }

    public void apply() {
        if (!fetched)
            return;
        for (int i = 0; i < 12; i++) {
            RenderSystem.setShaderTexture(i, texture[i]);
        }
    }

}
