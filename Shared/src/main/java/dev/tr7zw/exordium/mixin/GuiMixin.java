package dev.tr7zw.exordium.mixin;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import com.mojang.blaze3d.vertex.PoseStack;

import dev.tr7zw.exordium.access.ChatAccess;
import dev.tr7zw.exordium.util.BufferedComponent;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.gui.components.ChatComponent;

@Mixin(Gui.class)
public class GuiMixin {

    @Shadow
    private ChatComponent chat;
    
    @WrapOperation(method = "render", at = {
            @At(value = "INVOKE", target = "Lnet/minecraft/client/gui/components/ChatComponent;render(Lcom/mojang/blaze3d/vertex/PoseStack;III)V"),
    })
    private void renderChatWrapper(ChatComponent instance, PoseStack poseStack, int tickCount, int j, int k, final Operation<Void> operation) {
        ChatAccess chatAccess = (ChatAccess) chat;
        chatAccess.updateState(tickCount);
        BufferedComponent bufferedComponent = chatAccess.getBufferedComponent();
        if(!bufferedComponent.render()) {
            operation.call(instance, poseStack, tickCount, j, k);
        }
        bufferedComponent.renderEnd();
    }
    
}
