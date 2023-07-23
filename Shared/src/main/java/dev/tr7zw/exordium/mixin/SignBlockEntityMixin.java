package dev.tr7zw.exordium.mixin;

import org.spongepowered.asm.mixin.Mixin;

import com.mojang.blaze3d.vertex.PoseStack;

import dev.tr7zw.exordium.access.SignBufferHolder;
import dev.tr7zw.exordium.util.SignBufferRenderer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.network.chat.Component;
import net.minecraft.world.level.block.entity.HangingSignBlockEntity;
import net.minecraft.world.level.block.entity.SignBlockEntity;
import net.minecraft.world.level.block.entity.SignText;

@Mixin(SignBlockEntity.class)
public class SignBlockEntityMixin implements SignBufferHolder {

    private SignBufferRenderer cachedBufferRenderer = null;
    private SignText front;
    private SignText back;
    private int currentLight = -1;

    @Override
    public boolean renderBuffered(PoseStack poseStack, MultiBufferSource multiBufferSource, boolean renderFront, int light) {
        SignBlockEntity sign = (SignBlockEntity) (Object) this;
        if (isSignEmpty(sign.getFrontText()) || isSignEmpty(sign.getBackText())) {
            return true; // empty sign, nothing to do
        }
        if (cachedBufferRenderer == null || currentLight != light || (renderFront && (sign.getFrontText() != front))
                || (!renderFront && (sign.getBackText() != back))) {
            if (cachedBufferRenderer == null) {
                cachedBufferRenderer = new SignBufferRenderer(sign, light);
            }
            cachedBufferRenderer.refreshImage(sign, light, renderFront);
            currentLight = light;
        }
        cachedBufferRenderer.render(poseStack, light, ((Object) this) instanceof HangingSignBlockEntity, renderFront);
        return true;
    }

    private boolean isSignEmpty(SignText text) {
        for (int i = 0; i < 4; i++) {
            Component line = text.getMessage(i, false);
            if (!line.getString().isBlank())
                return false;
        }
        return true;
    }

}
