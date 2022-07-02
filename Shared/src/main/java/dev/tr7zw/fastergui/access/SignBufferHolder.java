package dev.tr7zw.fastergui.access;

import com.mojang.blaze3d.vertex.PoseStack;

import net.minecraft.client.renderer.MultiBufferSource;

public interface SignBufferHolder {

    public boolean renderBuffered(PoseStack poseStack, MultiBufferSource multiBufferSource, int light);

}
