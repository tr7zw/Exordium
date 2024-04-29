package dev.tr7zw.tests;

import com.llamalad7.mixinextras.MixinExtrasBootstrap;
import com.mojang.blaze3d.platform.GlStateManager;
import net.minecraft.SharedConstants;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.gui.components.ChatComponent;
import net.minecraft.client.gui.components.DebugScreenOverlay;
import net.minecraft.client.gui.screens.inventory.InventoryScreen;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.client.renderer.blockentity.SignRenderer;
import net.minecraft.client.renderer.entity.ItemRenderer;
import net.minecraft.client.renderer.entity.player.PlayerRenderer;
import net.minecraft.server.Bootstrap;
import net.minecraft.world.level.block.entity.SignBlockEntity;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.objenesis.Objenesis;
import org.objenesis.ObjenesisStd;

public class MixinTests {

    @BeforeAll
    public static void setup() {
        MixinExtrasBootstrap.init();
        SharedConstants.tryDetectVersion();
        Bootstrap.bootStrap();
    }

    @Test
    public void testMixins() {
        Objenesis objenesis = new ObjenesisStd();
        objenesis.newInstance(ChatComponent.class);
        objenesis.newInstance(DebugScreenOverlay.class);
        objenesis.newInstance(PlayerRenderer.class);
        objenesis.newInstance(GameRenderer.class);
        objenesis.newInstance(GlStateManager.class);
        objenesis.newInstance(Gui.class);
        objenesis.newInstance(InventoryScreen.class);
        objenesis.newInstance(ItemRenderer.class);
        objenesis.newInstance(Minecraft.class);
        objenesis.newInstance(SignBlockEntity.class);
        objenesis.newInstance(SignRenderer.class);
    }
}