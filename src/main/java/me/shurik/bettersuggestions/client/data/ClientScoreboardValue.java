package me.shurik.bettersuggestions.client.data;

import me.shurik.bettersuggestions.interfaces.ScoreboardValue;

// Client doesn't have access to `ScoreboardObjective`s so we store the name instead
public record ClientScoreboardValue(String objective, int score) implements ScoreboardValue {
    @Override
    public String getObjective() {
        return objective;
    }

    @Override
    public int getScore() {
        return score;
    }
}