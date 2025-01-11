package dev.tr7zw.exordium.mixin;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
//#if MC < 12006
//$$import org.spongepowered.asm.mixin.injection.Inject;
//$$import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
//#endif

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;

import dev.tr7zw.exordium.ExordiumModBase;
import dev.tr7zw.exordium.access.HealthAccess;
import dev.tr7zw.exordium.components.BufferInstance;
import dev.tr7zw.exordium.components.vanilla.HealthComponent;
import lombok.Getter;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.world.entity.LivingEntity;

@Mixin(Gui.class)
public class GuiHealthMixin implements HealthAccess {

    @Shadow
    private Minecraft minecraft;
    @Shadow
    @Getter
    private long healthBlinkTime;
    @Shadow
    @Getter
    private int lastHealth;
    @Shadow
    @Getter
    private int displayHealth;
    @Shadow
    @Getter
    private int tickCount;

    private boolean renderingMountHealth = false;

    //#if MC >= 12005
    @WrapOperation(method = "renderHotbarAndDecorations", at = {
            @At(value = "INVOKE", target = "Lnet/minecraft/client/gui/Gui;renderPlayerHealth(Lnet/minecraft/client/gui/GuiGraphics;)V"), })
    //#else
    //$$ @WrapOperation(method = "render", at = {
    //$$         @At(value = "INVOKE", target = "Lnet/minecraft/client/gui/Gui;renderPlayerHealth(Lnet/minecraft/client/gui/GuiGraphics;)V"), })
    //#endif
    private void renderPlayerHealthWrapper(Gui gui, GuiGraphics guiGraphics, final Operation<Void> operation) {
        BufferInstance<HealthAccess> buffer = ExordiumModBase.instance.getBufferManager()
                .getBufferInstance(HealthComponent.getId(), HealthAccess.class);
        if (!buffer.renderBuffer(tickCount, this, guiGraphics)) {
            operation.call(gui, guiGraphics);
            renderingMountHealth = true;
            renderVehicleHealth(guiGraphics);
            renderingMountHealth = false;
        }
        buffer.postRender(this, guiGraphics);
    }

    //#if MC >= 12005
    @WrapOperation(method = "renderHotbarAndDecorations", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/gui/Gui;renderVehicleHealth(Lnet/minecraft/client/gui/GuiGraphics;)V"))
    private void renderVehicleHealthHead(Gui gui, GuiGraphics guiGraphics, final Operation<Void> operation) {
        if (renderingMountHealth || !ExordiumModBase.instance.config.healthSettings.isEnabled()
                || minecraft.player.isCreative()) {
            // prevent rendering multiple times, just render into the texture
            operation.call(gui, guiGraphics);
        }
    }
    //#else
    //$$ @Inject(method = "renderVehicleHealth", at = @At("HEAD"), cancellable = true)
    //$$ private void renderVehicleHealthHead(GuiGraphics guiGraphics, CallbackInfo ci) {
    //$$     if (!renderingMountHealth && ExordiumModBase.instance.config.healthSettings.isEnabled()
    //$$             && !minecraft.player.isCreative()) {
    //$$         // prevent rendering multiple times, just render into the texture
    //$$         ci.cancel();
    //$$      }
    //$$  }
    //#endif

    @Shadow
    public void renderVehicleHealth(GuiGraphics guiGraphics) {
    }

    @Shadow
    private LivingEntity getPlayerVehicleWithHealth() {
        return null;
    };

    @Shadow
    public int getVehicleMaxHearts(LivingEntity livingEntity) {
        return 0;
    }

    public LivingEntity getExordiumPlayerVehicleWithHealth() {
        return getPlayerVehicleWithHealth();
    }

    public int getExordiumVehicleMaxHearts(LivingEntity livingEntity) {
        return getVehicleMaxHearts(livingEntity);
    }

}