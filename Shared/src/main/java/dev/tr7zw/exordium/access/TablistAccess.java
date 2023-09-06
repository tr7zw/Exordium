package dev.tr7zw.exordium.access;

import dev.tr7zw.exordium.access.VanillaBufferAccess.PlayerListOverlayAccess;
import net.minecraft.world.scores.Objective;
import net.minecraft.world.scores.Scoreboard;

public interface TablistAccess extends PlayerListOverlayAccess {
    public void updateState(Scoreboard scoreboard, Objective objective);

}
