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
import dev.tr7zw.exordium.access.VanillaBufferAccess;
import dev.tr7zw.exordium.components.BufferInstance;
import dev.tr7zw.exordium.render.LegacyBuffer;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.BossHealthOverlay;
import net.minecraft.client.gui.components.ChatComponent;
import net.minecraft.client.gui.components.PlayerTabOverlay;
import net.minecraft.world.scores.Objective;
import net.minecraft.world.scores.Scoreboard;

@Mixin(Gui.class)
public abstract class GuiMixin implements GuiAccess {

    @Shadow
    private ChatComponent chat;
    @Shadow
    private PlayerTabOverlay tabList;
    @Shadow
    protected int tickCount;

    @Shadow
    public abstract BossHealthOverlay getBossOverlay();

    @WrapOperation(method = "renderChat", at = {
            @At(value = "INVOKE", target = "Lnet/minecraft/client/gui/components/ChatComponent;render(Lnet/minecraft/client/gui/GuiGraphics;IIIZ)V"), })
    private void renderChatWrapper(ChatComponent instance, GuiGraphics guiGraphics, int tickCount, int j, int k,
            boolean b, final Operation<Void> operation) {
        ChatAccess chatAccess = (ChatAccess) chat;
        BufferInstance<ChatAccess> buffer = ExordiumModBase.instance.getBufferManager()
                .getBufferInstance(dev.tr7zw.exordium.components.vanilla.ChatComponent.getId(), ChatAccess.class);
        if (!buffer.renderBuffer(tickCount, chatAccess)) {
            operation.call(instance, guiGraphics, tickCount, j, k, b);
        }
        buffer.postRender(chatAccess);
    }

    @WrapOperation(method = "renderTabList", at = {
            @At(value = "INVOKE", target = "Lnet/minecraft/client/gui/components/PlayerTabOverlay;render(Lnet/minecraft/client/gui/GuiGraphics;ILnet/minecraft/world/scores/Scoreboard;Lnet/minecraft/world/scores/Objective;)V"), })
    private void renderTablistWrapper(PlayerTabOverlay instance, GuiGraphics guiGraphics, int screenWidth,
            Scoreboard scoreboard, Objective objective2, final Operation<Void> operation) {
        TablistAccess tablistAccess = (TablistAccess) tabList;
        tablistAccess.updateState(scoreboard, objective2);
        LegacyBuffer bufferedComponent = tablistAccess.getPlayerListOverlayBuffer();
        if (!bufferedComponent.render()) {
            operation.call(instance, guiGraphics, screenWidth, scoreboard, objective2);
        }
        bufferedComponent.renderEnd();
    }

    @WrapOperation(method = "method_55808", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/gui/components/BossHealthOverlay;render(Lnet/minecraft/client/gui/GuiGraphics;)V"))
    private void renderBossBarWrapper(BossHealthOverlay instance, GuiGraphics guiGraphics, Operation<Void> original) {
        VanillaBufferAccess.BossHealthOverlayAccess overlayAccess = (VanillaBufferAccess.BossHealthOverlayAccess) this
                .getBossOverlay();
        LegacyBuffer hotbarOverlayBuffer = overlayAccess.getHotbarOverlayBuffer();
        if (!hotbarOverlayBuffer.render()) {
            System.out.println("Re rendering");
            original.call(instance, guiGraphics);
        }
        hotbarOverlayBuffer.renderEnd();
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
