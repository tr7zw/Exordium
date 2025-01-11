package dev.tr7zw.exordium.util.rendersystem;

import com.mojang.blaze3d.systems.RenderSystem;

import lombok.Getter;
import lombok.ToString;
import net.minecraft.client.renderer.CompiledShaderProgram;

@ToString
public class ShaderHolder implements StateHolder {

    @Getter
    private boolean fetched = false;
    private CompiledShaderProgram shader = null;

    public void fetch() {
        fetched = true;
        shader = RenderSystem.getShader();
    }

    public void apply() {
        if (!fetched)
            return;
        //#if MC <= 12101
        //$$ RenderSystem.setShader(() -> shader);
        //#else
        RenderSystem.setShader(shader);
        //#endif
    }

}
