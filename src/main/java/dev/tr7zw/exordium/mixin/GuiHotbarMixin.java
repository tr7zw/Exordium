package dev.tr7zw.exordium.mixin;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;

import dev.tr7zw.exordium.ExordiumModBase;
import dev.tr7zw.exordium.access.VanillaBufferAccess.HotbarOverlayAccess;
import dev.tr7zw.exordium.render.BufferedComponent;
import dev.tr7zw.exordium.render.LegacyBuffer;
import net.minecraft.client.AttackIndicatorStatus;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.spectator.SpectatorGui;
import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;

@Mixin(Gui.class)
public class GuiHotbarMixin implements HotbarOverlayAccess {

    @Unique
    private boolean outdated = false;
    @Unique
    private float lastAttackState = 0;
    @Unique
    private final BakedModel[] hotbarModels = new BakedModel[10];
    @Unique
    private final int[] itemPopAnimation = new int[10];
    @Unique
    private final int[] itemAmount = new int[10];
    @Unique
    private final int[] itemDurability = new int[10];
    @Unique
    private int selectedSlot = 0;
    @Unique
    private boolean hasEnchantedItem = false;
    @Unique
    private boolean cooldownActive = false;

    @Unique
    private final LegacyBuffer hotbarBufferedComponent = new LegacyBuffer(
            () -> ExordiumModBase.instance.config.hotbarSettings) {

        @Override
        public boolean shouldRenderNextCappedFrame() {
            return outdated;
        }

        @Override
        public void captureState() {
            hasEnchantedItem = false;
            cooldownActive = false;
            lastAttackState = Minecraft.getInstance().player.getAttackStrengthScale(0.0F);
            Player player = getCameraPlayer();
            if (player == null)
                return;
            for (int m = 0; m < 9; m++) {
                store((player.getInventory()).items.get(m), m, player);
            }
            store(player.getOffhandItem(), 9, player);
            selectedSlot = player.getInventory().selected;
        }
    };

    @Unique
    private void store(ItemStack item, int id, Player player) {
        if (item != null && !item.isEmpty()) {
            hotbarModels[id] = Minecraft.getInstance().getItemRenderer().getModel(item, player.level(), player, 0);
            itemPopAnimation[id] = item.getPopTime();
            itemAmount[id] = item.getCount();
            itemDurability[id] = item.getDamageValue();
            if (item.isEnchanted()) {
                this.hasEnchantedItem = true;
            }
            if (player.getCooldowns().isOnCooldown(item.getItem())) {
                this.cooldownActive = true;
            }
        } else {
            hotbarModels[id] = null;
            itemPopAnimation[id] = 0;
            itemAmount[id] = 0;
            itemDurability[id] = -1;
        }
    }

    @Unique
    private boolean hasChanged(ItemStack item, int id, Player player) {
        if (item != null && !item.isEmpty()) {
            if (itemAmount[id] != item.getCount()) {
                return true;
            }
            if (itemPopAnimation[id] != item.getPopTime()) {
                return true;
            }
            if (itemDurability[id] != item.getDamageValue()) {
                return true;
            }
            if (Minecraft.getInstance().getItemRenderer().getModel(item, player.level(), player,
                    0) != hotbarModels[id]) {
                return true;
            }
        } else if (hotbarModels[id] != null) {
            return true;
        }
        return false;
    }

    @Unique
    public boolean hasChanged() {
        if (Minecraft.getInstance().options.attackIndicator().get() == AttackIndicatorStatus.HOTBAR) {
            float g = Minecraft.getInstance().player.getAttackStrengthScale(0.0F);
            if (g != lastAttackState) {
                return true;
            }
        }
        if (hasEnchantedItem || cooldownActive) {
            return true;
        }
        Player player = getCameraPlayer();
        if (player == null)
            return true;
        if (selectedSlot != player.getInventory().selected) {
            return true;
        }
        for (int m = 0; m < 9; m++) {
            ItemStack item = (player.getInventory()).items.get(m);
            if (hasChanged(item, m, player)) {
                return true;
            }
        }
        if (hasChanged(player.getOffhandItem(), 9, player)) {
            return true;
        }

        return false;
    }

    @WrapOperation(method = "renderHotbarAndDecorations", at = {
            @At(value = "INVOKE", target = "Lnet/minecraft/client/gui/Gui;renderItemHotbar(Lnet/minecraft/client/gui/GuiGraphics;F)V"), })
    private void renderHotbarWrapper(Gui gui, GuiGraphics guiGraphics, float f, final Operation<Void> operation) {
        outdated = hasChanged();
        if (!hotbarBufferedComponent.render()) {
            operation.call(gui, guiGraphics, f);
        }
        hotbarBufferedComponent.renderEnd();
    }

    @WrapOperation(method = "renderHotbarAndDecorations", at = {
            @At(value = "INVOKE", target = "Lnet/minecraft/client/gui/components/spectator/SpectatorGui;renderHotbar(Lnet/minecraft/client/gui/GuiGraphics;)V"), })
    private void renderHotbarWrapperSpectator(SpectatorGui gui, GuiGraphics guiGraphics,
            final Operation<Void> operation) {
        outdated = hasChanged();
        if (!hotbarBufferedComponent.render()) {
            operation.call(gui, guiGraphics);
        }
        hotbarBufferedComponent.renderEnd();
    }

    @Shadow
    private Player getCameraPlayer() {
        return null;
    }

    @Override
    public LegacyBuffer getHotbarOverlayBuffer() {
        return hotbarBufferedComponent;
    }

}
