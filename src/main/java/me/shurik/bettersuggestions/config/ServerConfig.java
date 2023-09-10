package me.shurik.bettersuggestions.config;

import me.shedaniel.autoconfig.annotation.ConfigEntry;

public class ServerConfig {
    @ConfigEntry.Gui.Tooltip(count = 2)
    public boolean requireOpToRequestData = true;
}
