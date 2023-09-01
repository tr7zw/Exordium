package dev.tr7zw.exordium.util;

import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

import com.google.common.collect.Iterables;
import com.google.common.collect.Lists;
import com.mojang.datafixers.util.Pair;

import net.minecraft.client.Minecraft;
import net.minecraft.network.chat.Component;
import net.minecraft.world.scores.Objective;
import net.minecraft.world.scores.PlayerTeam;
import net.minecraft.world.scores.Score;
import net.minecraft.world.scores.Scoreboard;

public class ScoreboardHelper {

    private static final Minecraft minecraft = Minecraft.getInstance();

    public static ScoreboardState getScoreboardData() {
        Scoreboard scoreboard = minecraft.level.getScoreboard();
        Objective objective = null;
        PlayerTeam playerTeam = scoreboard.getPlayersTeam(minecraft.player.getScoreboardName());
        if (playerTeam != null) {
            int n = playerTeam.getColor().getId();
            if (n >= 0)
                objective = scoreboard.getDisplayObjective(3 + n);
        }
        if (objective == null) {
            objective = scoreboard.getDisplayObjective(1);
        }
        if (objective == null) {
            return null;
        }
        Collection<Score> collection = scoreboard.getPlayerScores(objective);
        List<Score> list = (List<Score>) collection.stream()
                .filter(score -> (score.getOwner() != null && !score.getOwner().startsWith("#")))
                .collect(Collectors.toList());
        if (list.size() > 15) {
            collection = Lists.newArrayList(Iterables.skip(list, collection.size() - 15));
        } else {
            collection = list;
        }
        List<Pair<Integer, Component>> list2 = Lists.newArrayListWithCapacity(collection.size());
        Component title = objective.getDisplayName();
        for (Score score : collection) {
            PlayerTeam playerTeam2 = scoreboard.getPlayersTeam(score.getOwner());
            Component component2 = PlayerTeam.formatNameForTeam(playerTeam2, Component.literal(score.getOwner()));
            list2.add(Pair.of(score.getScore(), component2));
        }
        return new ScoreboardState(title, list2);
    }

    public static record ScoreboardState(Component title, List<Pair<Integer, Component>> entries) {
    }

}
