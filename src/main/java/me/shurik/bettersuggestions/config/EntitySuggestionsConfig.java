package me.shurik.bettersuggestions.config;

import me.shedaniel.autoconfig.annotation.ConfigEntry;

public class EntitySuggestionsConfig {
    @ConfigEntry.Gui.Tooltip(count = 2)
    public boolean suggestEntitySelector = false;
    @ConfigEntry.BoundedDiscrete(min = 1, max = 100)
    public int entitySuggestionRadius = 10;

    @ConfigEntry.Gui.PrefixText
    public boolean showEntityId = true;
    public boolean showEntityUuid = true;
    public boolean showEntityPos = true;
    public boolean showEntityTags = true;
    public boolean showEntityVehicle = true;
    public boolean showEntityPassengers = true;
    public boolean showEntityTeam = true;
    public boolean showEntityHealth = true;
}
