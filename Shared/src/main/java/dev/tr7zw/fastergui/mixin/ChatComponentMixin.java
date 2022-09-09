package dev.tr7zw.fastergui.mixin;

import java.util.List;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;

import dev.tr7zw.fastergui.access.ChatAccess;
import net.minecraft.client.GuiMessage;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.components.ChatComponent;
import net.minecraft.util.FormattedCharSequence;

@Mixin(ChatComponent.class)
public abstract class ChatComponentMixin implements ChatAccess {

    @Shadow
    private Minecraft minecraft;
    @Shadow
    private List<GuiMessage<FormattedCharSequence>> trimmedMessages;
    @Shadow
    private int chatScrollbarPos;
    
    @Override
    public boolean hasActiveAnimations(int i) {
        if (isChatHidden())
            return false;
        if(isChatFocused())
            return false;
        int j = getLinesPerPage();
        for (int o = 0; o + this.chatScrollbarPos < this.trimmedMessages.size() && o < j; o++) {
            GuiMessage<FormattedCharSequence> guiMessage = this.trimmedMessages.get(o + this.chatScrollbarPos);
            if (guiMessage != null) {
                int p = i - guiMessage.getAddedTime();
                if (p > 170 && p < 200) { // 180 is correct, add a tiny buffer for the frame to catch up
                        return true;
                }
            }
        }
        return false;
    }

    @Shadow
    public abstract boolean isChatFocused();
    
    @Shadow
    public abstract boolean isChatHidden();
    
    @Shadow
    public abstract int getLinesPerPage();
    
}
