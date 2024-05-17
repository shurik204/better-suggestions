package me.shurik.bettersuggestions.utils;

import me.shurik.bettersuggestions.Server;
import net.minecraft.scoreboard.*;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public class Scoreboards {
    public interface ScoreboardValue {
        String getObjective();
        int getScore();
    }

    public record ScoreboardScoreContainer(ScoreboardObjective objective, ReadableScoreboardScore score) implements ScoreboardValue {
        @Override
        public String getObjective() {
            return objective.getName();
        }

        @Override
        public int getScore() {
            return score.getScore();
        }
    }
    // Base methods
    public static ServerScoreboard getInstance() { return Server.INSTANCE.getScoreboard(); }
    public static Collection<ScoreboardObjective> getObjectives() { return getInstance().getObjectives(); }
    @Nullable
    public static ScoreboardObjective getObjective(String name) { return getObjectives().stream().filter(objective -> objective.getName().equals(name)).findFirst().orElse(null); }
    @Nullable
    public static ReadableScoreboardScore getScore(String name, ScoreboardObjective objective) { return getInstance().getScore(() -> name, objective); }

    // Copy of Scoreboard.getScoreboardEntries(objective) with a few changes
    public static Collection<ReadableScoreboardScore> getScores(ScoreboardObjective objective) {
        List<ReadableScoreboardScore> scoreboardScores = new ArrayList<>();
        getInstance().scores.forEach((scoreHolderName, scores) -> {
            ScoreboardScore scoreboardScore = scores.get(objective);
            if (scoreboardScore != null) {
                scoreboardScores.add(scoreboardScore);
            }
        });
        return scoreboardScores;
    }
    public static Collection<ScoreboardScoreContainer> getScores(String name) {
        Collection<ScoreboardScoreContainer> scores = new ArrayList<>();
        for (ScoreboardObjective objective : getObjectives()) {
            ReadableScoreboardScore score = getScore(name, objective);
            if (score != null) scores.add(new ScoreboardScoreContainer(objective, score));
        }
        return scores;
    }
    public static void setScore(String name, ScoreboardObjective objective, int value) { getInstance().getOrCreateScore(() -> name, objective).setScore(value); }
    //

    @Nullable
    public static ReadableScoreboardScore getScore(String name, String objectiveName) {
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