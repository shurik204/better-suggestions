package me.shurik.bettersuggestions;

import net.fabricmc.api.ModInitializer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import me.shurik.bettersuggestions.event.ServerEvents;
import me.shurik.bettersuggestions.networking.ModPackets;
import me.shurik.bettersuggestions.suggestion.ScoreHolderArgumentTypeSuggestions;

public class BetterSuggestionsMod implements ModInitializer {
	public static final Logger LOGGER = LoggerFactory.getLogger("better-suggestions");

	@Override
	public void onInitialize() {		
		ScoreHolderArgumentTypeSuggestions.init();
		ModPackets.init();
		ServerEvents.init();

		LOGGER.info("Suggestions!");
	}
}