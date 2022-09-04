package dev.tr7zw.fastergui.util;

import java.util.ArrayList;
import java.util.List;

import dev.tr7zw.fastergui.FasterGuiModBase;

/**
 * Iris causes issues when trying to switch render buffers during world
 * rendering. This class delays the draws to after the world rendering(causes a
 * 1 frame delay in signs, which isn't that bad).
 * 
 * @author tr7zw
 *
 */
public class DelayedRenderCallManager {

    private List<Runnable> renderCalls = new ArrayList<>();
    private List<Runnable> nametagRenderCalls = new ArrayList<>();
    
    public void addRenderCall(Runnable run) {
        renderCalls.add(run);
    }
    
    public void addNametagRenderCall(Runnable run) {
        nametagRenderCalls.add(run);
    }
    
    public void execRenderCalls() {
        for(Runnable run : renderCalls) {
            run.run();
        }
        renderCalls.clear();
        if(!nametagRenderCalls.isEmpty()) {
            NametagScreenBuffer buffer = FasterGuiModBase.instance.getNameTagScreenBuffer();
            buffer.bind();
            for(Runnable run : nametagRenderCalls) {
                run.run();
            }
            buffer.bindEnd();
            nametagRenderCalls.clear();
        }
    }
    
}
