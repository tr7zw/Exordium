package dev.tr7zw.exordium.util;

import com.mojang.blaze3d.shaders.Uniform;

import net.minecraft.client.renderer.ShaderInstance;

public class CustomShaderManager {

    private ShaderInstance positionMultiTexShader;
    private Uniform positionMultiTexShaderTextureCountUniform;

    public void registerShaderInstance(ShaderInstance shaderInstance) {
        this.positionMultiTexShader = shaderInstance;
        this.positionMultiTexShaderTextureCountUniform = shaderInstance.getUniform("texcount");
    }

    public ShaderInstance getPositionMultiTexShader() {
        return positionMultiTexShader;
    }

    public Uniform getPositionMultiTexTextureCountUniform() {
        return positionMultiTexShaderTextureCountUniform;
    }

}
