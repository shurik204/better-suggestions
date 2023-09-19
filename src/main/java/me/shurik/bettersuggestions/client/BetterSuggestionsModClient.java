package me.shurik.bettersuggestions.client;

import me.shurik.bettersuggestions.client.data.ClientDataGetter;
import me.shurik.bettersuggestions.client.event.ClientEvents;
import me.shurik.bettersuggestions.client.network.ClientPacketHandler;
import net.fabricmc.api.ClientModInitializer;

public class BetterSuggestionsModClient implements ClientModInitializer {
	@Override
	public void onInitializeClient() {
		ClientEvents.init();
		ClientDataGetter.init();
		ClientPacketHandler.init();
    }
}