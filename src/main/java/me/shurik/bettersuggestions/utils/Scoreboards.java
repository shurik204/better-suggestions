package me.shurik.bettersuggestions.utils;

import net.minecraft.scoreboard.ScoreboardObjective;
import net.minecraft.scoreboard.ScoreboardPlayerScore;
import net.minecraft.scoreboard.ServerScoreboard;
import net.minecraft.server.MinecraftServer;

import java.util.Collection;

public class Scoreboards {
    public static MinecraftServer server;

    // Base methods
    public static ServerScoreboard getInstance() { return server.getScoreboard(); }
    public static Collection<ScoreboardObjective> getObjectives() { return getInstance().getObjectives(); }
    public static ScoreboardObjective getObjective(String name) { return getInstance().getObjective(name); }
    public static ScoreboardPlayerScore getScore(String name, ScoreboardObjective objective) { return getInstance().getPlayerScore(name, objective); }
    public static Collection<ScoreboardPlayerScore> getScores(ScoreboardObjective objective) { return getInstance().getAllPlayerScores(objective); }
    public static void setScore(String name, ScoreboardObjective objective, int value) { getScore(name, objective).setScore(value); }
    //

    public static ScoreboardPlayerScore getScore(String name, String objective) { return getScore(name, getObjective(objective)); }
    public static void setScore(String name, String objective, int value) { setScore(name, getObjective(objective), value); };
}