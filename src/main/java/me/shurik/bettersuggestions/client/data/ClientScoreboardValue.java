package me.shurik.bettersuggestions.client.data;

// Client doesn't have access to `ScoreboardObjective`s so we store the name instead
public record ClientScoreboardValue(String objective, int score) {}