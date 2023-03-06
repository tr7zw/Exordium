package dev.tr7zw.exordium.mixin;

import org.spongepowered.asm.mixin.Mixin;

import com.mojang.blaze3d.vertex.PoseStack;

import dev.tr7zw.exordium.access.SignBufferHolder;
import dev.tr7zw.exordium.util.SignBufferRenderer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.network.chat.Component;
import net.minecraft.world.level.block.entity.SignBlockEntity;

@Mixin(SignBlockEntity.class)
public class SignBlockEntityMixin implements SignBufferHolder {

    private SignBufferRenderer cachedBufferRenderer = null;
    private Component[] lines = new Component[4];
    private int currentLight = -1;

    // TODO: hook the update methods instead of hot checking stuff.
    @Override
    public boolean renderBuffered(PoseStack poseStack, MultiBufferSource multiBufferSource, int light) {
        SignBlockEntity sign = (SignBlockEntity) (Object) this;
        if (isSignEmpty(sign)) {
            return true; // empty sign, nothing to do (yet)
        }
        if (cachedBufferRenderer == null || (currentLight != light && !sign.hasGlowingText())
                || lines[0] != sign.getMessage(0, false) || lines[1] != sign.getMessage(1, false)
                || lines[2] != sign.getMessage(2, false) || lines[3] != sign.getMessage(3, false)) {
            if (cachedBufferRenderer == null) {
                cachedBufferRenderer = new SignBufferRenderer(sign, light);
            }
            cachedBufferRenderer.refreshImage(sign, light);
            lines[0] = sign.getMessage(0, false);
            lines[1] = sign.getMessage(1, false);
            lines[2] = sign.getMessage(2, false);
            lines[3] = sign.getMessage(3, false);
            currentLight = light;
        }
        cachedBufferRenderer.render(poseStack, light);
        return true;
    }

    private boolean isSignEmpty(SignBlockEntity sign) {
        for (int i = 0; i < 4; i++) {
            Component line = sign.getMessage(i, false);
            if (!line.getString().isBlank())
                return false;
        }
        return true;
    }

}
