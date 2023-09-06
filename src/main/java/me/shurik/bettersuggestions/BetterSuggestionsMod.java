package me.shurik.bettersuggestions;

import com.google.common.collect.Lists;
import me.shedaniel.autoconfig.AutoConfig;
import me.shedaniel.autoconfig.serializer.JanksonConfigSerializer;
import me.shurik.bettersuggestions.config.ConfigStore;
import me.shurik.bettersuggestions.event.ServerEvents;
import me.shurik.bettersuggestions.suggestion.FunctionArgumentTypeSuggestions;
import me.shurik.bettersuggestions.suggestion.ScoreHolderArgumentTypeSuggestions;
import net.fabricmc.api.ModInitializer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class BetterSuggestionsMod implements ModInitializer {
	public static final String MOD_ID = "better-suggestions";
	public static final Logger LOGGER = LoggerFactory.getLogger(MOD_ID);

	public static final ConfigStore CONFIG;
	
	@Override
	public void onInitialize() {
		ScoreHolderArgumentTypeSuggestions.init();
		FunctionArgumentTypeSuggestions.init();
		ServerEvents.init();
		
		LOGGER.info("Suggestions!");
	}

	static {
		// Register config
		AutoConfig.register(ConfigStore.class, JanksonConfigSerializer::new);
		// Load it
		CONFIG = AutoConfig.getConfigHolder(ConfigStore.class).getConfig();
		// Update if needed
		if (CONFIG.version < 1) {
			CONFIG.version = 1;
			CONFIG.prioritizedSuggestions = Lists.newArrayList("minecraft:barrier", "data", "tellraw");
			AutoConfig.getConfigHolder(ConfigStore.class).save();
		}
	}
}