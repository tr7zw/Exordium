package dev.tr7zw.exordium.access;

import dev.tr7zw.exordium.util.BufferedComponent;
import net.minecraft.world.scores.Objective;
import net.minecraft.world.scores.Scoreboard;

public interface TablistAccess {
    public void updateState(Scoreboard scoreboard, Objective objective);

    public BufferedComponent getBufferedComponent();
}
