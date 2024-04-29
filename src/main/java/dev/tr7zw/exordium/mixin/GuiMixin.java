package dev.tr7zw.exordium.mixin;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;

import dev.tr7zw.exordium.ExordiumModBase;
import dev.tr7zw.exordium.access.ChatAccess;
import dev.tr7zw.exordium.access.GuiAccess;
import dev.tr7zw.exordium.access.TablistAccess;
import dev.tr7zw.exordium.util.BufferedComponent;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.ChatComponent;
import net.minecraft.client.gui.components.PlayerTabOverlay;
import net.minecraft.world.scores.Objective;
import net.minecraft.world.scores.Scoreboard;

@Mixin(Gui.class)
public class GuiMixin implements GuiAccess {

    @Shadow
    private ChatComponent chat;
    @Shadow
    private PlayerTabOverlay tabList;
    @Shadow
    protected int tickCount;

    @WrapOperation(method = "renderChat", at = {
            @At(value = "INVOKE", target = "Lnet/minecraft/client/gui/components/ChatComponent;render(Lnet/minecraft/client/gui/GuiGraphics;IIIZ)V"), })
    private void renderChatWrapper(ChatComponent instance, GuiGraphics guiGraphics, int tickCount, int j, int k,
            boolean b, final Operation<Void> operation) {
        ChatAccess chatAccess = (ChatAccess) chat;
        chatAccess.updateState(tickCount);
        BufferedComponent bufferedComponent = chatAccess.getChatOverlayBuffer();
        if (!bufferedComponent.render()) {
            operation.call(instance, guiGraphics, tickCount, j, k, b);
        }
        bufferedComponent.renderEnd();
    }

    @WrapOperation(method = "renderTabList", at = {
            @At(value = "INVOKE", target = "Lnet/minecraft/client/gui/components/PlayerTabOverlay;render(Lnet/minecraft/client/gui/GuiGraphics;ILnet/minecraft/world/scores/Scoreboard;Lnet/minecraft/world/scores/Objective;)V"), })
    private void renderTablistWrapper(PlayerTabOverlay instance, GuiGraphics guiGraphics, int screenWidth,
            Scoreboard scoreboard, Objective objective2, final Operation<Void> operation) {
        TablistAccess tablistAccess = (TablistAccess) tabList;
        tablistAccess.updateState(scoreboard, objective2);
        BufferedComponent bufferedComponent = tablistAccess.getPlayerListOverlayBuffer();
        if (!bufferedComponent.render()) {
            operation.call(instance, guiGraphics, screenWidth, scoreboard, objective2);
        }
        bufferedComponent.renderEnd();
    }

    @Inject(method = "render", at = @At(value = "TAIL"))
    public void render(GuiGraphics guiGraphics, float partialTick, CallbackInfo ci) {
        ExordiumModBase.instance.getDelayedRenderCallManager().renderComponents();
    }

    @Override
    public ChatComponent getChatComponent() {
        return chat;
    }

    @Override
    public PlayerTabOverlay getPlayerTabOverlay() {
        return tabList;
    }

    @Override
    public int getTickCount() {
        return tickCount;
    }

}
