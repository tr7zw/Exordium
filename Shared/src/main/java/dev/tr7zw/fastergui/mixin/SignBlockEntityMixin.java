package dev.tr7zw.fastergui.mixin;

import org.spongepowered.asm.mixin.Mixin;

import com.mojang.blaze3d.vertex.PoseStack;

import dev.tr7zw.fastergui.access.SignBufferHolder;
import dev.tr7zw.fastergui.util.SignBufferRenderer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.world.level.block.entity.SignBlockEntity;

@Mixin(SignBlockEntity.class)
public class SignBlockEntityMixin implements SignBufferHolder {

    private SignBufferRenderer cachedBufferRenderer = null;
    
    @Override
    public boolean renderBuffered(PoseStack poseStack, MultiBufferSource multiBufferSource) {
        if(cachedBufferRenderer == null) {
            cachedBufferRenderer = new SignBufferRenderer((SignBlockEntity)(Object)this, multiBufferSource);
        }
        cachedBufferRenderer.render(poseStack);
        return true;
    }

}
