package dev.tr7zw.exordium.components.vanilla;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import dev.tr7zw.exordium.access.TablistAccess;
import dev.tr7zw.exordium.components.BufferComponent;
import dev.tr7zw.util.NMSHelper;
import lombok.Getter;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.components.PlayerTabOverlay;
import net.minecraft.client.multiplayer.PlayerInfo;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.GameType;
import net.minecraft.world.scores.Objective;
import net.minecraft.world.scores.PlayerTeam;
import net.minecraft.world.scores.Scoreboard;
import net.minecraft.world.scores.criteria.ObjectiveCriteria;
//#if MC >= 12004
import net.minecraft.world.scores.PlayerScoreEntry;
//#else
//$$ import net.minecraft.world.scores.Score;
//#endif

public class PlayerListComponent
        implements BufferComponent<dev.tr7zw.exordium.components.vanilla.PlayerListComponent.PlayerListContext> {

    private static final Minecraft minecraft = Minecraft.getInstance();
    @Getter
    private static final ResourceLocation id = NMSHelper.getResourceLocation("minecraft", "player_list");

    private List<Integer> playerInfoHashes = new ArrayList<>();
    private int headerHash = 0;
    private int footerHash = 0;
    private int scoreboardHash = 0;
    private int objectiveHash = 0;
    private Objective lastTrackedObjective;

    public record PlayerListContext(TablistAccess tablist, Scoreboard scoreboard, Objective objective) {
    }

    @Override
    public void captureState(PlayerListContext context) {
        playerInfoHashes = fastGetPlayerInfoListHashCode(context, context.tablist().getPlayerInfosExordium());
        headerHash = context.tablist().getHeader() == null ? 0 : context.tablist().getHeader().getString().hashCode();
        footerHash = context.tablist().getFooter() == null ? 0 : context.tablist().getFooter().getString().hashCode();
    }

    @Override
    public boolean hasChanged(int tickCount, PlayerListContext context) {
        boolean scoreboardOrObjectiveChange = scoreboardOrObjectiveChanged(context.scoreboard, context.objective);
        int newHeaderHash = context.tablist().getHeader() == null ? 0
                : context.tablist().getHeader().getString().hashCode();
        int newFooterHash = context.tablist().getFooter() == null ? 0
                : context.tablist().getFooter().getString().hashCode();
        boolean plaverInfoOutdated = !playerInfoHashes
                .equals(fastGetPlayerInfoListHashCode(context, context.tablist().getPlayerInfosExordium()));
        return plaverInfoOutdated || headerHash != newHeaderHash || footerHash != newFooterHash
                || scoreboardOrObjectiveChange;
    }

    public boolean scoreboardOrObjectiveChanged(Scoreboard scoreboard, Objective objective) {
        if (objective == null && lastTrackedObjective == null)
            return false;

        int scoreboardHashCode = 1;
        //#if MC >= 12004
        for (PlayerScoreEntry score : scoreboard.listPlayerScores(objective))
            scoreboardHashCode = 31 * scoreboardHashCode + (score == null ? 0 : score.value());
        //#else
        //$$for (Score score : scoreboard.getPlayerScores(objective))
        //$$    scoreboardHashCode = 31 * scoreboardHashCode + (score == null ? 0 : score.getScore());
        //#endif

        int newObjectiveHashCode = objective == null ? 0 : objective.getName().hashCode();
        if (scoreboardHashCode == scoreboardHash && newObjectiveHashCode == objectiveHash)
            return false;
        scoreboardHash = scoreboardHashCode;
        objectiveHash = newObjectiveHashCode;
        lastTrackedObjective = objective;
        return true;
    }

    public List<Integer> fastGetPlayerInfoListHashCode(PlayerListContext context, List<PlayerInfo> playerInfos) {
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
                    PlayerTabOverlay.HealthState healthState = context.tablist().getHealthStates().computeIfAbsent(
                            playerInfo.getProfile().getId(),
                            (_uuid) -> new PlayerTabOverlay.HealthState(lastTrackedObjective.getScoreboard()
                                    //#if MC >= 12004
                                    .getOrCreatePlayerScore(player, lastTrackedObjective).get()));
                    //#else
                    //$$.getOrCreatePlayerScore(player.getName().toString(), lastTrackedObjective).getScore()));
                    //#endif
                    playerHash += healthState.isBlinking(context.tablist.getGui().getGuiTicks()) ? 63 : 127;
                }
            }
            hashCodes.add(playerHash);
        }
        return hashCodes;
    }

}
