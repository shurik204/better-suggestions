package me.shurik.bettersuggestions.client;

import it.unimi.dsi.fastutil.ints.Int2ObjectOpenHashMap;
import me.shurik.bettersuggestions.client.event.ClientEvents;
import me.shurik.bettersuggestions.client.network.ClientPacketHandler;
import me.shurik.bettersuggestions.client.utils.FallbackTagGetter;
import net.fabricmc.api.ClientModInitializer;
import net.minecraft.client.MinecraftClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Set;

public class BetterSuggestionsModClient implements ClientModInitializer {
	public static final Logger LOGGER = LoggerFactory.getLogger("better-suggestions");
	public static final MinecraftClient CLIENT = MinecraftClient.getInstance();

	public static boolean MOD_PRESENT_ON_SERVER = false;

	public static final Int2ObjectOpenHashMap<Set<String>> ENTITY_TAGS = new Int2ObjectOpenHashMap<>();

	@Override
	public void onInitializeClient() {

		ClientEvents.init();
		FallbackTagGetter.init();
		ClientPacketHandler.init();
	}
}