package dev.tr7zw.exordium.util;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import com.mojang.blaze3d.platform.GlStateManager;
import com.mojang.blaze3d.systems.RenderSystem;

import dev.tr7zw.exordium.ExordiumModBase;
import dev.tr7zw.exordium.render.BufferedComponent;
import dev.tr7zw.exordium.render.Model;
import dev.tr7zw.exordium.util.rendersystem.BlendStateHolder;
import dev.tr7zw.exordium.util.rendersystem.DepthStateHolder;
import dev.tr7zw.exordium.util.rendersystem.MultiStateHolder;
import dev.tr7zw.exordium.util.rendersystem.ShaderColorHolder;

/**
 * Iris causes issues when trying to switch render buffers during world
 * rendering. This class delays the draws to after the world rendering
 * 
 * @author tr7zw
 *
 */
public class DelayedRenderCallManager {
    private static final int MAX_TEXTURES_PER_DRAW = 8;
    private final List<BufferedComponent> componentRenderCalls = new ArrayList<>();
    private final MultiStateHolder stateHolder = new MultiStateHolder(new BlendStateHolder(), new DepthStateHolder(),
            new ShaderColorHolder());

    public void addBufferedComponent(BufferedComponent component) {
        this.componentRenderCalls.add(component);
    }

    public void renderComponents() {
        this.stateHolder.fetch();

        CustomShaderManager shaderManager = ExordiumModBase.instance.getCustomShaderManager();
        RenderSystem.disableDepthTest();
        RenderSystem.depthMask(false);
        RenderSystem.enableBlend();
        Objects.requireNonNull(shaderManager);

        //#if MC >= 12102
        RenderSystem.setShader(shaderManager.getPositionMultiTexShader());
        //#else
        //$$RenderSystem.setShader(shaderManager::getPositionMultiTexShader);
        //#endif
        RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, 1.0F);

        Model model = BufferedComponent.getModel();

        List<BufferedComponent> crosshairComponents = new ArrayList<>();
        List<BufferedComponent> normalComponents = new ArrayList<>();
        for (BufferedComponent component : this.componentRenderCalls) {
            if (((IBufferedComponent) component).getCrosshair()) {
                crosshairComponents.add(component);
            } else {
                normalComponents.add(component);
            }
        }

        if (!normalComponents.isEmpty()) {
            RenderSystem.blendFunc(GlStateManager.SourceFactor.ONE, GlStateManager.DestFactor.ONE_MINUS_SRC_ALPHA);
            drawBatch(normalComponents, shaderManager, model);
        }
        if (!crosshairComponents.isEmpty()) {
            RenderSystem.blendFuncSeparate(GlStateManager.SourceFactor.ONE_MINUS_DST_COLOR,
                    GlStateManager.DestFactor.ONE_MINUS_SRC_COLOR, GlStateManager.SourceFactor.ONE,
                    GlStateManager.DestFactor.ZERO);
            drawBatch(crosshairComponents, shaderManager, model);
        }

        this.stateHolder.apply();
        this.componentRenderCalls.clear();
    }

    private void drawBatch(List<BufferedComponent> components, CustomShaderManager shaderManager, Model model) {
        int textureId = 0;
        for (BufferedComponent component : components) {
            RenderSystem.setShaderTexture(textureId, component.getTextureId());
            ++textureId;

            if (textureId == 8) {
                shaderManager.getPositionMultiTexTextureCountUniform().set(8);
                model.draw(RenderSystem.getModelViewMatrix());
                textureId = 0;
            }
        }
        if (textureId > 0) {
            shaderManager.getPositionMultiTexTextureCountUniform().set(textureId);
            model.draw(RenderSystem.getModelViewMatrix());
        }
    }
}
