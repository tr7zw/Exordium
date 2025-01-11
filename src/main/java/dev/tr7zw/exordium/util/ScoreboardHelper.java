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
import net.minecraft.world.scores.Objective;
import net.minecraft.world.scores.PlayerTeam;
import net.minecraft.world.scores.Scoreboard;
//#if MC >= 12004
import net.minecraft.network.chat.numbers.NumberFormat;
import net.minecraft.world.scores.PlayerScoreEntry;
//#else
//$$ import net.minecraft.world.scores.Score;
//#endif
//#if MC >= 12002
import net.minecraft.world.scores.DisplaySlot;
//#endif

@UtilityClass
public class ScoreboardHelper {

    private static final Minecraft MINECRAFT = Minecraft.getInstance();

    public static ScoreboardState getScoreboardData() {
        Scoreboard scoreboard = MINECRAFT.level.getScoreboard();
        Objective objective = null;
        PlayerTeam playerTeam = scoreboard.getPlayersTeam(MINECRAFT.player.getScoreboardName());
        if (playerTeam != null) {
            int n = playerTeam.getColor().getId();
            if (n >= 0) {
                //#if MC >= 12002
                objective = scoreboard.getDisplayObjective(DisplaySlot.BY_ID.apply(3 + n));
                //#else
                //$$ objective = scoreboard.getDisplayObjective(3 + n);
                //#endif
            }
        }
        if (objective == null) {
            //#if MC >= 12002
            objective = scoreboard.getDisplayObjective(DisplaySlot.SIDEBAR);
            //#else
            //$$ objective = scoreboard.getDisplayObjective(1);
            //#endif
        }
        if (objective == null) {
            return null;
        }

        //#if MC >= 12004
        Collection<PlayerScoreEntry> collection = scoreboard.listPlayerScores(objective);
        List<PlayerScoreEntry> list = collection.stream().filter(score -> !score.owner().startsWith("#"))
                .collect(Collectors.toList());
        //#else
        //$$Collection<Score> collection = scoreboard.getPlayerScores(objective);
        //$$List<Score> list = (List<Score>) collection.stream()
        //$$        .filter(score -> (score.getOwner() != null && !score.getOwner().startsWith("#")))
        //$$        .collect(Collectors.toList());
        //#endif

        if (list.size() > 15) {
            collection = Lists.newArrayList(Iterables.skip(list, collection.size() - 15));
        } else {
            collection = list;
        }

        Component title = objective.getDisplayName();
        //#if MC >= 12004
        List<Pair<Component, Component>> list2 = Lists.newArrayListWithCapacity(collection.size());
        for (PlayerScoreEntry score : collection) {
            PlayerTeam playerTeam2 = scoreboard.getPlayersTeam(score.owner());
            Component component2 = PlayerTeam.formatNameForTeam(playerTeam2, score.ownerName());
            NumberFormat format = score.numberFormatOverride() == null ? objective.numberFormat()
                    : score.numberFormatOverride();
            list2.add(Pair.of(
                    format == null ? Component.literal(Integer.toString(score.value())) : format.format(score.value()),
                    component2));

        }
        //#else
        //$$List<Pair<Integer, Component>> list2 = Lists.newArrayListWithCapacity(collection.size());
        //$$for (Score score : collection) {
        //$$    PlayerTeam playerTeam2 = scoreboard.getPlayersTeam(score.getOwner());
        //$$    Component component2 = PlayerTeam.formatNameForTeam(playerTeam2, Component.literal(score.getOwner()));
        //$$    list2.add(Pair.of(score.getScore(), component2));
        //$$}
        //#endif
        return new ScoreboardState(title, list2);
    }

    //#if MC >= 12004
    public record ScoreboardState(Component title, List<Pair<Component, Component>> entries) {
    }
    //#else
    //$$public record ScoreboardState(Component title, List<Pair<Integer, Component>> entries) {}
    //#endif
}
