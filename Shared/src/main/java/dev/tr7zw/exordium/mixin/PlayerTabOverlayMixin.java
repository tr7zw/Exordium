package dev.tr7zw.exordium.mixin;

import java.util.*;

import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.GameType;
import net.minecraft.world.scores.*;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;

import dev.tr7zw.exordium.ExordiumModBase;
import dev.tr7zw.exordium.access.TablistAccess;
import dev.tr7zw.exordium.util.BufferedComponent;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.gui.components.PlayerTabOverlay;
import net.minecraft.client.multiplayer.PlayerInfo;
import net.minecraft.network.chat.Component;
import net.minecraft.world.scores.criteria.ObjectiveCriteria;

@Mixin(PlayerTabOverlay.class)
public abstract class PlayerTabOverlayMixin implements TablistAccess {
    @Shadow
    private Minecraft minecraft;
    @Shadow
    private Gui gui;
    @Shadow
    private Map<UUID, PlayerTabOverlay.HealthState> healthStates;
    private ArrayList<Integer> playerInfoHashes = new ArrayList<>();
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
    private BufferedComponent playerlistBufferedComponent = new BufferedComponent(true,
            () -> ExordiumModBase.instance.config.tablistSettings) {

        @Override
        public boolean needsRender() {
            return outdated;
        }

        @Override
        public void captureState() {
            playerInfoHashes = fastGetPlayerInfoListHashCode(getPlayerInfos());
            headerHash = header == null ? 0 : header.getString().hashCode();
            footerHash = footer == null ? 0 : footer.getString().hashCode();
        }
    };

    @Override
    public void updateState(Scoreboard scoreboard, Objective objective) {
        boolean scoreboardOrObjectiveChange = scoreboardOrObjectiveChanged(scoreboard, objective);
        int newHeaderHash = header == null ? 0 : header.getString().hashCode();
        int newFooterHash = footer == null ? 0 : footer.getString().hashCode();
        boolean plaverInfoOutdated = !playerInfoHashes.equals(fastGetPlayerInfoListHashCode(getPlayerInfos()));
        outdated = plaverInfoOutdated || headerHash != newHeaderHash || footerHash != newFooterHash
                || scoreboardOrObjectiveChange;
    }

    public boolean scoreboardOrObjectiveChanged(Scoreboard scoreboard, Objective objective) {
        if (objective == null && lastTrackedObjective == null)
            return false;

        int scoreboardHashCode = 1;
        for (PlayerScoreEntry score : scoreboard.listPlayerScores(objective))
            scoreboardHashCode = 31 * scoreboardHashCode + (score == null ? 0 : score.value());

        int newObjectiveHashCode = objective == null ? 0 : objective.getName().hashCode();
        if (scoreboardHashCode == scoreboardHash && newObjectiveHashCode == objectiveHash)
            return false;
        scoreboardHash = scoreboardHashCode;
        objectiveHash = newObjectiveHashCode;
        lastTrackedObjective = objective;
        return true;
    }

    public ArrayList<Integer> fastGetPlayerInfoListHashCode(List<PlayerInfo> playerInfos) {
        ArrayList<Integer> hashCodes = new ArrayList<>();
        for (PlayerInfo playerInfo : playerInfos) {
            if (playerInfo == null)
                continue;

            int playerHash = playerInfo.getProfile().getId().hashCode();
            playerHash += playerInfo.getProfile().getName().hashCode();
            if (playerInfo.getTabListDisplayName() != null) {
                playerHash += playerInfo.getTabListDisplayName().getString().hashCode();
                playerHash += playerInfo.getTabListDisplayName().getStyle().hashCode();
            } else {
                PlayerTeam playerTeam = playerInfo.getTeam();
                if (playerTeam == null)
                    continue;
                Component prefix = playerTeam.getPlayerPrefix();
                Component suffix = playerTeam.getPlayerSuffix();
                playerHash += Objects.hash(playerTeam.getColor(), prefix.getStyle(), prefix.getString(),
                        suffix.getStyle(), suffix.getString());
            }
            playerHash += playerInfo.getGameMode() == GameType.SPECTATOR ? 31 : 0;
            playerHash += playerInfo.getSkin().texture().hashCode();
            playerHash += playerInfo.getLatency() * 63;

            if (lastTrackedObjective != null
                    && lastTrackedObjective.getRenderType() == ObjectiveCriteria.RenderType.HEARTS) {
                Player player = minecraft.level.getPlayerByUUID(playerInfo.getProfile().getId());

                if (player != null) {
                    PlayerTabOverlay.HealthState healthState = this.healthStates.computeIfAbsent(
                            playerInfo.getProfile().getId(),
                            (_uuid) -> new PlayerTabOverlay.HealthState(lastTrackedObjective.getScoreboard()
                                    .getOrCreatePlayerScore(player, lastTrackedObjective).get()));
                    playerHash += healthState.isBlinking(this.gui.getGuiTicks()) ? 63 : 127;
                }
            }
            hashCodes.add(playerHash);
        }
        return hashCodes;
    }

    @Shadow
    public abstract List<PlayerInfo> getPlayerInfos();

    @Override
    public BufferedComponent getPlayerListOverlayBuffer() {
        return playerlistBufferedComponent;
    }

}
