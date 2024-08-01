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
public abstract class ChatComponentMixin implements ChatAccess {

    @Getter
    @Final
    @Shadow
    private List<GuiMessage.Line> trimmedMessages;
    @Shadow
    private int chatScrollbarPos;

    @Override
    public int getChatScollbarPos() {
        return chatScrollbarPos;
    }

    @Override
    @Shadow
    public abstract boolean isChatFocused();

    @Override
    @Shadow
    public abstract boolean isChatHidden();

    @Override
    @Shadow
    public abstract int getLinesPerPage();

}
