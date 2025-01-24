package dev.tr7zw.exordium.mixin.support;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Pseudo;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import dev.tr7zw.exordium.ExordiumModBase;
import dev.tr7zw.exordium.components.BufferInstance;
import dev.tr7zw.exordium.components.support.PaperDollComponent;

@Pseudo
@Mixin(targets = "dev.tr7zw.paperdoll.PaperDollRenderer")
public class PaperDollMixin {

    @Inject(method = "render", at = @At("HEAD"), cancellable = true)
    private void renderCrosshairStart(float delta, CallbackInfo ci) {
        BufferInstance<PaperDollComponent> buffer = ExordiumModBase.instance.getBufferManager()
                .getBufferInstance(PaperDollComponent.getId(), PaperDollComponent.class);
        if (buffer.renderBuffer(0, null, null)) {
            ci.cancel();
        }

    }

    @Inject(method = "render", at = @At("RETURN"))
    private void renderCrosshairEnd(float delta, CallbackInfo ci) {
        BufferInstance<PaperDollComponent> buffer = ExordiumModBase.instance.getBufferManager()
                .getBufferInstance(PaperDollComponent.getId(), PaperDollComponent.class);
        buffer.postRender(null, null);
    }

}
