package dev.tr7zw.exordium.mixin;

import java.util.List;

import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;

import dev.tr7zw.exordium.access.ChatAccess;
import lombok.Getter;
import net.minecraft.client.GuiMessage;
import net.minecraft.client.gui.components.ChatComponent;

@Mixin(ChatComponent.class)
public class ChatComponentMixin implements ChatAccess {

    @Getter
    @Final
    @Shadow
    private List<GuiMessage.Line> trimmedMessages;
    @Shadow
    private int chatScrollbarPos;

//    @Unique
//    private int lastScrollbarPos = 0;
//    @Unique
//    private int messageCount = 0;
//    @Unique
//    private boolean wasFocused = false;
//    @Unique
//    private GuiMessage.Line lastMessage = null;
//
//    @Unique
//    boolean outdated = false;
//
//    @Unique
//    private final BufferedComponent chatBufferedComponent = new BufferedComponent(
//            () -> ExordiumModBase.instance.config.chatSettings) {
//
//        @Override
//        public boolean shouldRenderNextCappedFrame() {
//            return outdated;
//        }
//
//        @Override
//        public void captureState() {
//            lastMessage = trimmedMessages.isEmpty() ? null : trimmedMessages.get(0);
//            lastScrollbarPos = chatScrollbarPos;
//            messageCount = trimmedMessages.size();
//            wasFocused = isChatFocused();
//        }
//    };
//
//    @Unique
//    public boolean hasChanged(int i) {
//        GuiMessage.Line msg = trimmedMessages.isEmpty() ? null : trimmedMessages.get(0);
//        boolean changed = chatScrollbarPos != lastScrollbarPos || messageCount != trimmedMessages.size()
//                || isChatFocused() != wasFocused || msg != lastMessage;
//        if (changed) {
//            return true;
//        }
//        int j = getLinesPerPage();
//        for (int o = 0; o + this.chatScrollbarPos < this.trimmedMessages.size() && o < j; o++) {
//            GuiMessage.Line guiMessage = this.trimmedMessages.get(o + this.chatScrollbarPos);
//            if (guiMessage != null) {
//                int p = i - guiMessage.addedTime();
//                if (p > 170 && p < 200) { // 180 is correct, add a tiny buffer for the frame to catch up
//                    return true;
//                }
//            }
//        }
//        return false;
//    }
//
//    public void updateState(int tickCount) {
//        outdated = hasChanged(tickCount);
//    }

    @Override
    public int getChatScollbarPos() {
        return chatScrollbarPos;
    }

    @Override
    @Shadow
    public boolean isChatFocused() {
        // TODO Auto-generated method stub
        return false;
    }

    @Override
    @Shadow
    public boolean isChatHidden() {
        // TODO Auto-generated method stub
        return false;
    }

    @Override
    @Shadow
    public int getLinesPerPage() {
        // TODO Auto-generated method stub
        return 0;
    }

//    @Override
//    public BufferedComponent getChatOverlayBuffer() {
//        return chatBufferedComponent;
//    }

}
