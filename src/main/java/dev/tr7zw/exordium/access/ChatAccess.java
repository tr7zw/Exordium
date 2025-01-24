package dev.tr7zw.exordium.access;

import java.util.List;

import net.minecraft.client.GuiMessage;

public interface ChatAccess {

    List<GuiMessage.Line> getTrimmedMessages();

    int getChatScollbarPos();

    boolean isChatFocused();

    boolean isChatHidden();

    int getLinesPerPage();

    int getTickCount();

    void setTickCount(int ticks);

}
