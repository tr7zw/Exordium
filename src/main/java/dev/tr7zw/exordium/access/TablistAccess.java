package dev.tr7zw.exordium.access;

import net.minecraft.world.scores.Objective;
import net.minecraft.world.scores.Scoreboard;

public interface TablistAccess extends VanillaBufferAccess.PlayerListOverlayAccess {
    void updateState(Scoreboard scoreboard, Objective objective);

}
