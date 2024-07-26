package dev.tr7zw.exordium.access;

import net.minecraft.client.gui.components.ChatComponent;
import net.minecraft.client.gui.components.PlayerTabOverlay;

public interface GuiAccess {

    ChatComponent getChatComponent();

    PlayerTabOverlay getPlayerTabOverlay();

    int getTickCount();

}
