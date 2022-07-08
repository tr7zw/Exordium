package dev.tr7zw.fastergui.mixin;

import org.spongepowered.asm.mixin.Mixin;

import com.mojang.blaze3d.vertex.PoseStack;

import dev.tr7zw.fastergui.access.NametagBufferHolder;
import dev.tr7zw.fastergui.util.NametagBufferRenderer;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.Entity;

@Mixin(Entity.class)
public class EntityMixin implements NametagBufferHolder {

    private String lastNametag = null;
    
    private NametagBufferRenderer nametagBuffer = null;
    
    @Override
    public boolean renderBuffered(Component text, PoseStack arg3, MultiBufferSource arg4, int light, boolean sneaking) {
        if(nametagBuffer == null) { // lazy init
            int size = Minecraft.getInstance().font.width(text);
            if(size <= 0) {
                return false;
            }
            nametagBuffer = new NametagBufferRenderer();
        }
        if(lastNametag == null || (!lastNametag.equals(text.toString()))) {
            nametagBuffer.refreshImage(text, arg4, light);
            lastNametag = text.toString();
        }
        if(sneaking) {
            nametagBuffer.render(arg3, light, true, true);
        } else {
            nametagBuffer.render(arg3, light, true, false);
            nametagBuffer.render(arg3, light, false, true);
        }
        return true;
    }

}
