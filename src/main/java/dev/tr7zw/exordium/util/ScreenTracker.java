package dev.tr7zw.exordium.util;

import com.mojang.blaze3d.pipeline.RenderTarget;

import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import net.minecraft.client.Minecraft;

@RequiredArgsConstructor
public class ScreenTracker {

    @NonNull
    private final RenderTarget target;
    private int guiScale = 0;

    @SuppressWarnings("resource")
    public boolean hasChanged() {
        return guiScale != Minecraft.getInstance().options.guiScale().get()
                || target.width != Minecraft.getInstance().getWindow().getWidth()
                || target.height != Minecraft.getInstance().getWindow().getHeight();
    }

    public void updateState() {
        //#if MC >= 12102
        target.resize(Minecraft.getInstance().getWindow().getWidth(), Minecraft.getInstance().getWindow().getHeight());
        //#else
        //$$target.resize(Minecraft.getInstance().getWindow().getWidth(), Minecraft.getInstance().getWindow().getHeight(),
        //$$        true);
        //#endif
        guiScale = Minecraft.getInstance().options.guiScale().get();
    }

}
