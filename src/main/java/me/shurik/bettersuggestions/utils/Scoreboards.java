package me.shurik.bettersuggestions.utils;

import me.shurik.bettersuggestions.Server;
import net.minecraft.scoreboard.ScoreboardObjective;
import net.minecraft.scoreboard.ScoreboardPlayerScore;
import net.minecraft.scoreboard.ServerScoreboard;
import org.jetbrains.annotations.Nullable;

import java.util.Collection;

public class Scoreboards {
    // Base methods
    public static ServerScoreboard getInstance() { return Server.INSTANCE.getScoreboard(); }
    public static Collection<ScoreboardObjective> getObjectives() { return getInstance().getObjectives(); }
    @Nullable
    public static ScoreboardObjective getObjective(String name) { return getObjectives().stream().filter(objective -> objective.getName().equals(name)).findFirst().orElse(null); }
    public static ScoreboardPlayerScore getScore(String name, ScoreboardObjective objective) { return getInstance().getPlayerScore(name, objective); }
    public static Collection<ScoreboardPlayerScore> getScores(ScoreboardObjective objective) { return getInstance().getAllPlayerScores(objective); }
    public static Collection<ScoreboardPlayerScore> getScores(String name) { return getInstance().getPlayerObjectives(name).values(); }
    public static void setScore(String name, ScoreboardObjective objective, int value) { getScore(name, objective).setScore(value); }
    //

    @Nullable
    public static ScoreboardPlayerScore getScore(String name, String objectiveName) {
        ScoreboardObjective objective = getObjective(objectiveName);
        if (objective == null) return null;
        return getScore(name, objective);
    }
    public static boolean setScore(String name, String objectiveName, int value) {
        ScoreboardObjective scoreboardObjective = getObjective(objectiveName);
        if (scoreboardObjective == null) return false;
        setScore(name, scoreboardObjective, value);
        return true;
    }
}