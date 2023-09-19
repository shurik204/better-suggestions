package me.shurik.bettersuggestions.config;

import me.shedaniel.autoconfig.annotation.ConfigEntry;

public class FunctionSuggestionsConfig {
    @ConfigEntry.Gui.Tooltip(count = 3)
    public boolean hideUnderscoreFunctions = false;
}
