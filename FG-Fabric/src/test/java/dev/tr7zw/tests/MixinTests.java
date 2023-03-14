package dev.tr7zw.tests;

import static org.junit.jupiter.api.Assertions.assertNotEquals;

import java.util.List;
import java.util.Set;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.objenesis.Objenesis;
import org.objenesis.ObjenesisStd;

import com.mojang.blaze3d.platform.GlStateManager;

import dev.tr7zw.config.CustomConfigScreen;
import dev.tr7zw.exordium.Config;
import dev.tr7zw.exordium.ExordiumModBase;
import net.minecraft.SharedConstants;
import net.minecraft.client.Minecraft;
import net.minecraft.client.OptionInstance;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.gui.components.ChatComponent;
import net.minecraft.client.gui.components.DebugScreenOverlay;
import net.minecraft.client.gui.screens.inventory.InventoryScreen;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.client.renderer.blockentity.SignRenderer;
import net.minecraft.client.renderer.entity.ItemRenderer;
import net.minecraft.client.renderer.entity.player.PlayerRenderer;
import net.minecraft.locale.Language;
import net.minecraft.server.Bootstrap;
import net.minecraft.world.level.block.entity.SignBlockEntity;

public class MixinTests {

    @BeforeAll
    public static void setup() {
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
    
    @Test
    public void langTests() throws Throwable {
        Language lang = TestUtil.loadDefault("/assets/exordium/lang/en_us.json");
        ExordiumModBase.instance = new TestMod();
        ExordiumModBase.instance.config = new Config();
        CustomConfigScreen screen = (CustomConfigScreen) ExordiumModBase.instance.createConfigScreen(null);
        List<OptionInstance<?>> options = TestUtil.bootStrapCustomConfigScreen(screen);
        assertNotEquals(screen.getTitle().getString(), lang.getOrDefault(screen.getTitle().getString()));
        for(OptionInstance<?> option : options) {
            Set<String> keys = TestUtil.getKeys(option, true);
            for(String key : keys) {
                System.out.println(key + " " + lang.getOrDefault(key));
                assertNotEquals(key, lang.getOrDefault(key));
            }
        }
    }

}