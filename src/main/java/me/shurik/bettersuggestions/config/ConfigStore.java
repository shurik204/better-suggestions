package me.shurik.bettersuggestions.config;

import me.shedaniel.autoconfig.ConfigData;
import me.shedaniel.autoconfig.annotation.Config;
import me.shedaniel.autoconfig.annotation.ConfigEntry;

import java.util.List;

import static me.shurik.bettersuggestions.BetterSuggestionsMod.MOD_ID;

@Config(name = MOD_ID)
public class ConfigStore implements ConfigData {
    @ConfigEntry.Gui.Excluded
    public int version = 0;

    @ConfigEntry.BoundedDiscrete(min = 2, max = 50)
    public int maxSuggestionsShown = 12;

    @ConfigEntry.Gui.Tooltip(count = 2)
    public boolean suggestEntitySelector = false;

    @ConfigEntry.Gui.Tooltip(count = 3)
    public boolean hideUnderscoreFunctions = false;

    @ConfigEntry.Gui.Tooltip(count = 2)
    public boolean rememberCommandOnEscape = false;

    @ConfigEntry.BoundedDiscrete(min = 1, max = 100)
    public int entitySuggestionRadius = 10;

    // Wish there was an option to make lists expanded by default :pensive:
    public List<String> prioritizedSuggestions;
}
