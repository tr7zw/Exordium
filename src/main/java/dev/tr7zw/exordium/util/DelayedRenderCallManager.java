package dev.tr7zw.exordium.util;

import java.util.ArrayList;
import java.util.List;

import com.mojang.blaze3d.platform.GlStateManager;
import com.mojang.blaze3d.systems.RenderSystem;

import dev.tr7zw.exordium.ExordiumModBase;
import dev.tr7zw.exordium.render.BufferedComponent;
import dev.tr7zw.exordium.render.Model;

/**
 * Iris causes issues when trying to switch render buffers during world
 * rendering. This class delays the draws to after the world rendering(causes a
 * 1 frame delay in signs, which isn't that bad).
 * 
 * @author tr7zw
 *
 */
public class DelayedRenderCallManager {
    private static final int MAX_TEXTURES_PER_DRAW = 8;
    private final List<BufferedComponent> componentRenderCalls = new ArrayList<>();
    private final BlendStateHolder blendStateHolder = new BlendStateHolder();

    public void addBufferedComponent(BufferedComponent component) {
        this.componentRenderCalls.add(component);
    }

    public void renderComponents() {
        blendStateHolder.fetch();
        CustomShaderManager shaderManager = ExordiumModBase.instance.getCustomShaderManager();
        RenderSystem.disableDepthTest();
        RenderSystem.depthMask(false);
        RenderSystem.enableBlend();
        RenderSystem.blendFunc(GlStateManager.SourceFactor.ONE, GlStateManager.DestFactor.ONE_MINUS_SRC_ALPHA);
        //#if MC >= 12102
        RenderSystem.setShader(shaderManager.getPositionMultiTexShader());
        //#else
        //$$RenderSystem.setShader(shaderManager::getPositionMultiTexShader);
        //#endif
        RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, 1.0F);
        Model model = BufferedComponent.getModel();
        int textureId = 0;
        for (BufferedComponent component : componentRenderCalls) {
            RenderSystem.setShaderTexture(textureId, component.getTextureId());
            textureId++;
            if (textureId == MAX_TEXTURES_PER_DRAW) {
                shaderManager.getPositionMultiTexTextureCountUniform().set(MAX_TEXTURES_PER_DRAW);
                model.draw(RenderSystem.getModelViewMatrix());
                textureId = 0;
            }
        }
        if (textureId > 0) {
            shaderManager.getPositionMultiTexTextureCountUniform().set(textureId);
            model.draw(RenderSystem.getModelViewMatrix());
        }
        RenderSystem.depthMask(true);
        RenderSystem.enableDepthTest();
        RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, 1.0F);
        blendStateHolder.apply();
        componentRenderCalls.clear();
    }
}
