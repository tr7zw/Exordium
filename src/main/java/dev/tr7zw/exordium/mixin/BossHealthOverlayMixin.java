package dev.tr7zw.exordium.mixin;

import java.util.Map;
import java.util.UUID;

import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;

import dev.tr7zw.exordium.access.BossOverlayAccess;
import lombok.Getter;
import net.minecraft.client.gui.components.BossHealthOverlay;
import net.minecraft.client.gui.components.LerpingBossEvent;

@Mixin(BossHealthOverlay.class)
public class BossHealthOverlayMixin implements BossOverlayAccess {

    @Shadow
    @Final
    @Getter
    private Map<UUID, LerpingBossEvent> events;

}
