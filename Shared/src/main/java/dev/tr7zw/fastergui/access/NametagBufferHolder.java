package dev.tr7zw.fastergui.access;

import com.mojang.blaze3d.vertex.PoseStack;

import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.network.chat.Component;

public interface NametagBufferHolder {

    /**
     * Renders the nametag.
     * 
     * @param text the text to render
     * @param poseStack the pose stack to render the nametag with
     * @param buffer the buffer to render the nametag with
     * @param light the light to render the nametag with
     * @param discrete whether the nametag is discrete or not
     * @return whether the nametag was rendered or not
     */
    public boolean renderBuffered(Component arg2, PoseStack arg3, MultiBufferSource arg4, int light, boolean discrete);
    
}
