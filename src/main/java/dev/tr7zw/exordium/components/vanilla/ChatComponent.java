package dev.tr7zw.exordium.components.vanilla;

import dev.tr7zw.exordium.access.ChatAccess;
import dev.tr7zw.exordium.components.BufferComponent;
import dev.tr7zw.util.NMSHelper;
import lombok.Getter;
import net.minecraft.client.GuiMessage;
import net.minecraft.resources.ResourceLocation;

public class ChatComponent implements BufferComponent<ChatAccess> {

    @Getter
    private static final ResourceLocation id = NMSHelper.getResourceLocation("minecraft", "chat_panel");

    private int lastScrollbarPos = 0;
    private int messageCount = 0;
    private boolean wasFocused = false;
    private GuiMessage.Line lastMessage = null;
    private int lastTimer = 0;

    @Override
    public void captureState(ChatAccess context) {
        lastMessage = context.getTrimmedMessages().isEmpty() ? null : context.getTrimmedMessages().get(0);
        lastScrollbarPos = context.getChatScollbarPos();
        messageCount = context.getTrimmedMessages().size();
        wasFocused = context.isChatFocused();
        lastTimer = context.getTickCount();
    }

    @Override
    public boolean hasChanged(ChatAccess context) {
        GuiMessage.Line msg = context.getTrimmedMessages().isEmpty() ? null : context.getTrimmedMessages().get(0);
        boolean changed = context.getChatScollbarPos() != lastScrollbarPos
                || messageCount != context.getTrimmedMessages().size() || context.isChatFocused() != wasFocused
                || msg != lastMessage;
        if (changed) {
            return true;
        }
        int j = context.getLinesPerPage();
        for (int o = 0; o + context.getChatScollbarPos() < context.getTrimmedMessages().size() && o < j; o++) {
            GuiMessage.Line guiMessage = context.getTrimmedMessages().get(o + context.getChatScollbarPos());
            if (guiMessage != null) {
                int p = context.getTickCount() - guiMessage.addedTime();
                if (p > 170 && p < 200) { // 180 is correct, add a tiny buffer for the frame to catch up
                    return true;
                }
                int lastDelay = lastTimer - guiMessage.addedTime();
                if (lastDelay < 200 && p > 170) {
                    // time jumped from under 200 ticks to now being after the fadeout, update!
                    // This happens when you keep the chat open in 1.21.4+
                    return true;
                }
            }
        }
        return false;
    }

}
