package dev.tr7zw.exordium.mixin;

import dev.tr7zw.exordium.ExordiumModBase;
import dev.tr7zw.exordium.access.BossEventBufferAccess;
import dev.tr7zw.exordium.util.BufferedComponent;
import net.minecraft.client.gui.components.LerpingBossEvent;
import net.minecraft.network.chat.Component;
import net.minecraft.world.BossEvent;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;

import java.util.UUID;

@Mixin(LerpingBossEvent.class)
public abstract class LerpingBossEventMixin extends BossEvent implements BossEventBufferAccess {
    @Unique
    private UUID exordium_storedId;
    @Unique
    protected Component exordium_storedName;
    @Unique
    protected float exordium_storedProgress;
    @Unique
    protected BossEvent.BossBarColor exordium_storedColor;
    @Unique
    protected BossEvent.BossBarOverlay exordium_storedOverlay;
    @Unique
    protected boolean exordium_storedDarkenScreen;

    public LerpingBossEventMixin(UUID id, Component name, BossBarColor color, BossBarOverlay overlay) {
        super(id, name, color, overlay);
    }

    @Unique
    public void exordium_captureState() {
        this.exordium_storedId = this.getId();
        this.exordium_storedName = this.getName();
        this.exordium_storedProgress = this.getProgress();
        this.exordium_storedColor = this.color;
        this.exordium_storedOverlay = this.overlay;
        this.exordium_storedDarkenScreen = this.darkenScreen;
    }

    @Unique
    public boolean exordium_needsRender() {
        return this.exordium_storedId != this.getId() || !this.exordium_storedName.equals(this.getName())
                || this.exordium_storedProgress != this.getProgress()
                || !this.exordium_storedColor.equals(this.getColor())
                || !this.exordium_storedOverlay.equals(this.getOverlay())
                || this.exordium_storedDarkenScreen != this.darkenScreen;
    }
}
