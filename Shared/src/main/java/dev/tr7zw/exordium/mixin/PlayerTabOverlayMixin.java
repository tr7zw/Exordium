package dev.tr7zw.exordium.mixin;

import dev.tr7zw.exordium.ExordiumModBase;
import dev.tr7zw.exordium.access.TablistAccess;
import dev.tr7zw.exordium.util.BufferedComponent;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.PlayerInfo;
import net.minecraft.network.chat.Component;
import net.minecraft.world.scores.Objective;
import net.minecraft.world.scores.Score;
import net.minecraft.world.scores.Scoreboard;
import org.spongepowered.asm.mixin.Mixin;
import net.minecraft.client.gui.components.PlayerTabOverlay;
import org.spongepowered.asm.mixin.Shadow;

import java.util.List;

@Mixin(PlayerTabOverlay.class)
public abstract class PlayerTabOverlayMixin implements TablistAccess {
    @Shadow
    private Minecraft minecraft;
    private int playerInfoHash = 0;
    private int headerHash = 0;
    private int footerHash = 0;
    private int scoreboardHash = 0;
    private int objectiveHash = 0;
    private boolean scoreboardOrObjectiveChange = false;
    @Shadow
    private Component header;
    @Shadow
    private Component footer;
    private Objective lastTrackedObjective;

    private BufferedComponent bufferedComponent = new BufferedComponent(true, ExordiumModBase.instance.config.tablistSettings) {

        @Override
        public boolean needsRender() {
            return playerInfoHash != fastGetPlayerInfoListHashCode(getPlayerInfos()) || headerHash != header.getString().hashCode() || footerHash != footer.getString().hashCode() || scoreboardOrObjectiveChange;
        }

        @Override
        public void captureState() {
            playerInfoHash = fastGetPlayerInfoListHashCode(getPlayerInfos());
            headerHash = header.getString().hashCode();
            footerHash = footer.getString().hashCode();
        }
    };

    public void updateState(Scoreboard scoreboard, Objective objective) {
        scoreboardOrObjectiveChange = scoreboardOrObjectiveChanged(scoreboard, objective);
    }

    public boolean scoreboardOrObjectiveChanged(Scoreboard scoreboard, Objective objective) {
        if(objective == null && lastTrackedObjective == null) return false;

        int scoreboardHashCode = 1;
        for (Score score : scoreboard.getPlayerScores(objective))
            scoreboardHashCode = 31 * scoreboardHashCode + (score == null ? 0 : score.getScore());
        boolean scoreboardChanged = scoreboardHashCode != scoreboardHash;

        int newObjectiveHashCode = objective == null ? 0 : objective.getName().hashCode();
        if(!scoreboardChanged && newObjectiveHashCode == objectiveHash) return false;
        scoreboardHash = scoreboardHashCode;
        objectiveHash = newObjectiveHashCode;
        lastTrackedObjective = objective;
        return true;
    }

    public int fastGetPlayerInfoListHashCode(List<PlayerInfo> playerInfos) {
        int hashCode = 1;
        for (PlayerInfo playerInfo : playerInfos)
            hashCode = 31 * hashCode + (playerInfo == null ? 0 : playerInfo.getProfile().getId().hashCode());
        return hashCode;
    }

    public BufferedComponent getBufferedComponent() {
        return bufferedComponent;
    }

    @Shadow
    public abstract List<PlayerInfo> getPlayerInfos();
}
