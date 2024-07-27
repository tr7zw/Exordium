package dev.tr7zw.exordium.util;

import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

import com.google.common.collect.Iterables;
import com.google.common.collect.Lists;
import com.mojang.datafixers.util.Pair;

import lombok.experimental.UtilityClass;
import net.minecraft.client.Minecraft;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.numbers.NumberFormat;
import net.minecraft.world.scores.DisplaySlot;
import net.minecraft.world.scores.Objective;
import net.minecraft.world.scores.PlayerScoreEntry;
import net.minecraft.world.scores.PlayerTeam;
import net.minecraft.world.scores.Scoreboard;

@UtilityClass
public class ScoreboardHelper {

    private static final Minecraft MINECRAFT = Minecraft.getInstance();

    public static ScoreboardState getScoreboardData() {
        Scoreboard scoreboard = MINECRAFT.level.getScoreboard();
        Objective objective = null;
        PlayerTeam playerTeam = scoreboard.getPlayersTeam(MINECRAFT.player.getScoreboardName());
        if (playerTeam != null) {
            int n = playerTeam.getColor().getId();
            if (n >= 0)
                objective = scoreboard.getDisplayObjective(DisplaySlot.BY_ID.apply(3 + n));
        }
        if (objective == null) {
            objective = scoreboard.getDisplayObjective(DisplaySlot.SIDEBAR);
        }
        if (objective == null) {
            return null;
        }

        Collection<PlayerScoreEntry> collection = scoreboard.listPlayerScores(objective);
        List<PlayerScoreEntry> list = collection.stream().filter(score -> !score.owner().startsWith("#"))
                .collect(Collectors.toList());

        if (list.size() > 15) {
            collection = Lists.newArrayList(Iterables.skip(list, collection.size() - 15));
        } else {
            collection = list;
        }
        List<Pair<Component, Component>> list2 = Lists.newArrayListWithCapacity(collection.size());
        Component title = objective.getDisplayName();
        for (PlayerScoreEntry score : collection) {
            PlayerTeam playerTeam2 = scoreboard.getPlayersTeam(score.owner());
            Component component2 = PlayerTeam.formatNameForTeam(playerTeam2, score.ownerName());
            NumberFormat format = score.numberFormatOverride() == null ? objective.numberFormat()
                    : score.numberFormatOverride();
            list2.add(Pair.of(format == null ? Component.literal(Integer.toString(score.value())) : format.format(score.value()),
                    component2));

        }
        return new ScoreboardState(title, list2);
    }

    public record ScoreboardState(Component title, List<Pair<Component, Component>> entries) {
    }

}
