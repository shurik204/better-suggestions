package me.shurik.bettersuggestions.config;

import me.shedaniel.autoconfig.ConfigData;
import me.shedaniel.autoconfig.annotation.Config;
import me.shedaniel.autoconfig.annotation.ConfigEntry;

import java.util.List;

import static me.shurik.bettersuggestions.ModConstants.MOD_ID;

@Config(name = MOD_ID)
public class ConfigStore implements ConfigData {
    @ConfigEntry.Gui.Excluded
    public int version = 0;

    @ConfigEntry.Category("entitySuggestions")
    @ConfigEntry.Gui.TransitiveObject
    public EntitySuggestionsConfig entitySuggestions = new EntitySuggestionsConfig();

    @ConfigEntry.Category("functionSuggestions")
    @ConfigEntry.Gui.TransitiveObject
    public FunctionSuggestionsConfig functionSuggestions = new FunctionSuggestionsConfig();

    @ConfigEntry.Category("server")
    @ConfigEntry.Gui.TransitiveObject
    public ServerConfig server = new ServerConfig();

    @ConfigEntry.BoundedDiscrete(min = 2, max = 50)
    public int maxSuggestionsShown = 12;

    @ConfigEntry.Gui.Tooltip(count = 1)
    public boolean highlightCoordinates = true;

    @ConfigEntry.Gui.Tooltip(count = 2)
    public boolean rememberCommandOnEscape = false;

    // Wish there was an option to make lists expanded by default :pensive:
    public List<String> prioritizedSuggestions;
}