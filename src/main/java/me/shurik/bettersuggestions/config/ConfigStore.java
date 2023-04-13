package me.shurik.bettersuggestions.config;

import static me.shurik.bettersuggestions.BetterSuggestionsMod.MOD_ID;

import java.util.List;

import me.shedaniel.autoconfig.ConfigData;
import me.shedaniel.autoconfig.annotation.Config;
import me.shedaniel.autoconfig.annotation.ConfigEntry;

@Config(name = MOD_ID)
public class ConfigStore implements ConfigData {
    @ConfigEntry.Gui.Excluded
    public int version = 0;

    @ConfigEntry.BoundedDiscrete(min = 2, max = 50)
    public int maxSuggestionsShown = 12;

    @ConfigEntry.Gui.Tooltip(count = 2)
    public boolean suggestEntitySelector = false;


    // @ConfigEntry.ColorPicker
    // public int chatSuggestionsColor = -805306368;

    // Wish there was an option to make lists exapnded by default :pensive:
    public List<String> prioritizedSuggestions;
}
