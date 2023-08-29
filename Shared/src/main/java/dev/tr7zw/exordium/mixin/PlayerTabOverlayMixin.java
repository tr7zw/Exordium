package dev.tr7zw.exordium.mixin;

import dev.tr7zw.exordium.ExordiumModBase;
import dev.tr7zw.exordium.access.TablistAccess;
import dev.tr7zw.exordium.util.BufferedComponent;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.multiplayer.PlayerInfo;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.Style;
import net.minecraft.world.scores.Objective;
import net.minecraft.world.scores.Score;
import net.minecraft.world.scores.Scoreboard;
import net.minecraft.world.scores.criteria.ObjectiveCriteria;
import org.spongepowered.asm.mixin.Mixin;
import net.minecraft.client.gui.components.PlayerTabOverlay;
import org.spongepowered.asm.mixin.Shadow;

import java.util.List;
import java.util.Map;
import java.util.UUID;

@Mixin(PlayerTabOverlay.class)
public abstract class PlayerTabOverlayMixin implements TablistAccess {
    @Shadow
    private Minecraft minecraft;
    @Shadow
    private Gui gui;
    @Shadow
    private Map<UUID, PlayerTabOverlay.HealthState> healthStates;
    private int playerInfoHash = 0;
    private int headerHash = 0;
    private int footerHash = 0;
    private int scoreboardHash = 0;
    private int objectiveHash = 0;
    @Shadow
    private Component header;
    @Shadow
    private Component footer;
    private Objective lastTrackedObjective;
    private boolean outdated;
    private BufferedComponent bufferedComponent = new BufferedComponent(true, ExordiumModBase.instance.config.tablistSettings) {

        @Override
        public boolean needsRender() {
            return outdated;
        }

        @Override
        public void captureState() {
            playerInfoHash = fastGetPlayerInfoListHashCode(getPlayerInfos());
            headerHash = header == null ? 0 : header.getString().hashCode();
            footerHash = footer == null ? 0 : footer.getString().hashCode();
        }
    };

    public void updateState(Scoreboard scoreboard, Objective objective) {
        boolean scoreboardOrObjectiveChange = scoreboardOrObjectiveChanged(scoreboard, objective);
        int newHeaderHash = header == null ? 0 : header.getString().hashCode();
        int newFooterHash = footer == null ? 0 : footer.getString().hashCode();
        outdated = playerInfoHash != fastGetPlayerInfoListHashCode(getPlayerInfos()) || headerHash != newHeaderHash || footerHash != newFooterHash || scoreboardOrObjectiveChange;
    }

    public boolean scoreboardOrObjectiveChanged(Scoreboard scoreboard, Objective objective) {
        if (objective == null && lastTrackedObjective == null) return false;

        int scoreboardHashCode = 1;
        for (Score score : scoreboard.getPlayerScores(objective))
            scoreboardHashCode = 31 * scoreboardHashCode + (score == null ? 0 : score.getScore());

        int newObjectiveHashCode = objective == null ? 0 : objective.getName().hashCode();
        if (scoreboardHashCode == scoreboardHash && newObjectiveHashCode == objectiveHash) return false;
        scoreboardHash = scoreboardHashCode;
        objectiveHash = newObjectiveHashCode;
        lastTrackedObjective = objective;
        return true;
    }

    public int fastGetPlayerInfoListHashCode(List<PlayerInfo> playerInfos) {
        int hashCode = 1;
        for (PlayerInfo playerInfo : playerInfos) {
            int combinedHashes = 0;
            if (playerInfo == null) {
                hashCode *= 31;
                continue;
            }
            combinedHashes += playerInfo.getProfile().getId().hashCode();
            if (playerInfo.getTabListDisplayName() != null) {
                combinedHashes += playerInfo.getTabListDisplayName().getString().hashCode();
                Style displaynameStyle = playerInfo.getTabListDisplayName().getStyle();
                combinedHashes += (displaynameStyle.getColor() == null ? 0 : displaynameStyle.getColor().getValue()) + (displaynameStyle.isBold() ? 1 : 0) + (displaynameStyle.isItalic() ? 3 : 0) + (displaynameStyle.isObfuscated() ? 7 : 0) + (displaynameStyle.isUnderlined() ? 15 : 0) + (displaynameStyle.isStrikethrough() ? 31 : 0);
                combinedHashes += playerInfo.getTabListDisplayName().getStyle().hashCode();
            }
            combinedHashes += playerInfo.getSkinLocation().hashCode();
            combinedHashes += playerInfo.getLatency() * 63;

            if (lastTrackedObjective != null && lastTrackedObjective.getRenderType() == ObjectiveCriteria.RenderType.HEARTS) {
                PlayerTabOverlay.HealthState healthState = this.healthStates.computeIfAbsent(playerInfo.getProfile().getId(), (uUID) ->
                        new PlayerTabOverlay.HealthState(lastTrackedObjective.getScoreboard().getOrCreatePlayerScore(playerInfo.getProfile().getName(), lastTrackedObjective).getScore()));
                combinedHashes = 31 * combinedHashes + (healthState.isBlinking(this.gui.getGuiTicks()) ? 63 : 127);
            }
            hashCode = 31 * hashCode + combinedHashes;
        }
        return hashCode;
    }

    public BufferedComponent getBufferedComponent() {
        return bufferedComponent;
    }

    @Shadow
    public abstract List<PlayerInfo> getPlayerInfos();
}
