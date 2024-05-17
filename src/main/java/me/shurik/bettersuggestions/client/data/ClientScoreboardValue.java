package me.shurik.bettersuggestions.client.data;

import me.shurik.bettersuggestions.utils.Scoreboards;

// Client doesn't have access to `ScoreboardObjective`s so we store the name instead
public record ClientScoreboardValue(String objective, int score) implements Scoreboards.ScoreboardValue {
    @Override
    public String getObjective() {
        return objective;
    }

    @Override
    public int getScore() {
        return score;
    }
}