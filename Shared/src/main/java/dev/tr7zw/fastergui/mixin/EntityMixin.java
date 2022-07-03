package dev.tr7zw.fastergui.mixin;

import org.spongepowered.asm.mixin.Mixin;

import com.google.common.base.Objects;
import com.mojang.blaze3d.vertex.PoseStack;

import dev.tr7zw.fastergui.access.NametagBufferHolder;
import dev.tr7zw.fastergui.util.NametagBufferRenderer;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.Entity;

@Mixin(Entity.class)
public class EntityMixin implements NametagBufferHolder {

    private Component lastNametag = null;
    private Boolean lastDiscrete = null;
    
    private NametagBufferRenderer nametagBuffer = null;
    
    @Override
    public boolean renderBuffered(Component text, PoseStack arg3, MultiBufferSource arg4, int light, boolean discrete) {
        if(nametagBuffer == null) { // lazy init
            int size = Minecraft.getInstance().font.width(text);
            if(size <= 0) {
                return false;
            }
            nametagBuffer = new NametagBufferRenderer();
            System.out.println("new buffer");
        }
        if(lastNametag == null || (!lastNametag.getString().equals(text.getString())) || this.lastDiscrete != discrete) {
            nametagBuffer.refreshImage(text, arg4, light, discrete);
            lastNametag = text;
            this.lastDiscrete = discrete;
            System.out.println("refresh");
        }
        nametagBuffer.render(arg3, light);
        return true;
    }

}
