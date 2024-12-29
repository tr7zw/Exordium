package dev.tr7zw.exordium.util;

import com.mojang.blaze3d.shaders.Uniform;

import lombok.Getter;
import net.minecraft.client.renderer.CompiledShaderProgram;

public class CustomShaderManager {

    @Getter
    private CompiledShaderProgram positionMultiTexShader;
    private Uniform positionMultiTexShaderTextureCountUniform;

    public void registerShaderInstance(CompiledShaderProgram shaderInstance) {
        this.positionMultiTexShader = shaderInstance;
        this.positionMultiTexShaderTextureCountUniform = shaderInstance.getUniform("texcount");
    }

    public Uniform getPositionMultiTexTextureCountUniform() {
        return positionMultiTexShaderTextureCountUniform;
    }

}
