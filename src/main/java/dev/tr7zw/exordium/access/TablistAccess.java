package dev.tr7zw.exordium.access;

import java.util.List;
import java.util.Map;
import java.util.UUID;

import net.minecraft.client.gui.Gui;
import net.minecraft.client.gui.components.PlayerTabOverlay;
import net.minecraft.client.multiplayer.PlayerInfo;
import net.minecraft.network.chat.Component;

public interface TablistAccess {

    Gui getGui();

    Map<UUID, PlayerTabOverlay.HealthState> getHealthStates();

    Component getHeader();

    Component getFooter();

    List<PlayerInfo> getPlayerInfosExordium();

}
