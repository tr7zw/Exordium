package dev.tr7zw.exordium.mixin;

import java.util.List;
import java.util.Map;
import java.util.UUID;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;

import dev.tr7zw.exordium.access.TablistAccess;
import lombok.Getter;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.gui.components.PlayerTabOverlay;
import net.minecraft.client.multiplayer.PlayerInfo;
import net.minecraft.network.chat.Component;

@Mixin(PlayerTabOverlay.class)
public class PlayerTabOverlayMixin implements TablistAccess {

    @Shadow
    @Getter
    private Gui gui;
    @Shadow
    @Getter
    private Map<UUID, PlayerTabOverlay.HealthState> healthStates;
    @Shadow
    @Getter
    private Component header;
    @Shadow
    @Getter
    private Component footer;

    @Shadow
    public List<PlayerInfo> getPlayerInfos() {
        return null;
    }

    @Override
    public List<PlayerInfo> getPlayerInfosExordium() {
        return getPlayerInfos();
    }

}
