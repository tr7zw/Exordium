package dev.tr7zw.exordium.mixin;

import java.util.List;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;

import dev.tr7zw.exordium.ExordiumModBase;
import dev.tr7zw.exordium.access.ChatAccess;
import dev.tr7zw.exordium.util.BufferedComponent;
import net.minecraft.client.GuiMessage;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.components.ChatComponent;

@Mixin(ChatComponent.class)
public abstract class ChatComponentMixin implements ChatAccess {

    @Shadow
    private Minecraft minecraft;
    @Shadow
    private List<GuiMessage.Line> trimmedMessages;
    @Shadow
    private int chatScrollbarPos;
    
    private int lastScrollbarPos = 0;
    private int messageCount = 0;
    private boolean wasFocused = false;
    private GuiMessage.Line lastMessage = null;
    
    boolean outdated = false;
    
    private BufferedComponent bufferedComponent = new BufferedComponent(ExordiumModBase.instance.config.chatSettings) {
        
        @Override
        public boolean needsRender() {
            return outdated;
        }

        @Override
        public void captureState() {
            lastMessage = trimmedMessages.isEmpty() ? null : trimmedMessages.get(0);
            lastScrollbarPos = chatScrollbarPos;
            messageCount = trimmedMessages.size();
            wasFocused = isChatFocused();
        }
    };
    
    public boolean hasChanged(int i) {
        GuiMessage.Line msg = trimmedMessages.isEmpty() ? null : trimmedMessages.get(0);
        boolean changed = chatScrollbarPos != lastScrollbarPos || messageCount != trimmedMessages.size() || isChatFocused() != wasFocused || msg != lastMessage;
        if(changed) {
            return true;
        }
        int j = getLinesPerPage();
        for (int o = 0; o + this.chatScrollbarPos < this.trimmedMessages.size() && o < j; o++) {
            GuiMessage.Line guiMessage = this.trimmedMessages.get(o + this.chatScrollbarPos);
            if (guiMessage != null) {
                int p = i - guiMessage.addedTime();
                if (p > 170 && p < 200) { // 180 is correct, add a tiny buffer for the frame to catch up
                        return true;
                }
            }
        }
        return false;
    }
    
    public void updateState(int tickCount) {
        outdated = hasChanged(tickCount);
    }
    
    public BufferedComponent getBufferedComponent() {
        return bufferedComponent;
    }

    @Shadow
    public abstract boolean isChatFocused();
    
    @Shadow
    public abstract boolean isChatHidden();
    
    @Shadow
    public abstract int getLinesPerPage();
    
}
