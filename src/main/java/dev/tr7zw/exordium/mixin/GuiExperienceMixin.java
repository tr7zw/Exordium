package dev.tr7zw.exordium.mixin;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;

import dev.tr7zw.exordium.ExordiumModBase;
import dev.tr7zw.exordium.components.BufferInstance;
import dev.tr7zw.exordium.components.vanilla.ExperienceComponent;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.gui.GuiGraphics;

@Mixin(Gui.class)
public class GuiExperienceMixin {

    @Shadow
    private Minecraft minecraft;

    @WrapOperation(method = "renderHotbarAndDecorations", at = {
            @At(value = "INVOKE", target = "Lnet/minecraft/client/gui/Gui;renderExperienceBar(Lnet/minecraft/client/gui/GuiGraphics;I)V"), })
    private void renderExperienceBarWrapper(Gui gui, GuiGraphics guiGraphics, int i, final Operation<Void> operation) {
        BufferInstance<Void> buffer = ExordiumModBase.instance.getBufferManager()
                .getBufferInstance(ExperienceComponent.getId(), Void.class);
        if (!buffer.renderBuffer(0, null, guiGraphics)) {
            operation.call(gui, guiGraphics, i);
        }
        buffer.postRender(null, guiGraphics);
    }

}
