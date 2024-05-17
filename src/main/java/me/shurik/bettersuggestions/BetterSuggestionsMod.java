package me.shurik.bettersuggestions;

import me.shurik.bettersuggestions.network.ServerPacketHandler;
import me.shurik.bettersuggestions.suggestion.FunctionArgumentTypeSuggestions;
import me.shurik.bettersuggestions.suggestion.ScoreHolderArgumentTypeSuggestions;
import net.fabricmc.api.ModInitializer;
import net.minecraft.util.Identifier;

public class BetterSuggestionsMod implements ModInitializer {
	static {
		// Load config
		ModConstants.loadConfig();
	}
	@Override
	public void onInitialize() {
		// Init custom suggestions
		ScoreHolderArgumentTypeSuggestions.init();
		FunctionArgumentTypeSuggestions.init();
		// Register server packet handler
		ServerPacketHandler.init();

		ModConstants.LOGGER.info("Suggestions!");
	}

	public static Identifier id(String path) {
		return new Identifier(ModConstants.MOD_ID, path);
	}
}