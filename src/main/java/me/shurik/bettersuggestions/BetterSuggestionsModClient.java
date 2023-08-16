package me.shurik.bettersuggestions;

import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayConnectionEvents;
import net.fabricmc.fabric.api.client.rendering.v1.WorldRenderEvents;
import net.minecraft.client.MinecraftClient;
import net.minecraft.entity.decoration.DisplayEntity;
import net.minecraft.entity.decoration.InteractionEntity;

import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import it.unimi.dsi.fastutil.ints.Int2ObjectOpenHashMap;
import me.shurik.bettersuggestions.access.HighlightableEntityAccessor;
import me.shurik.bettersuggestions.networking.ModPackets;
import me.shurik.bettersuggestions.render.SpecialHighlightRenderer;
import me.shurik.bettersuggestions.utils.FallbackTagGetter;

public class BetterSuggestionsModClient implements ClientModInitializer {
	public static final Logger LOGGER = LoggerFactory.getLogger("better-suggestions");
	public static final MinecraftClient CLIENT = MinecraftClient.getInstance();

	public static boolean MOD_PRESENT_ON_SERVER = false;

	public static final Int2ObjectOpenHashMap<Set<String>> ENTITY_TAGS = new Int2ObjectOpenHashMap<>();

	@Override
	public void onInitializeClient() {
		WorldRenderEvents.BEFORE_DEBUG_RENDER.register((worldrendercontext) -> {
			CLIENT.world.getEntities().forEach((entity) -> {
				if ((entity instanceof InteractionEntity || entity instanceof DisplayEntity) && ((HighlightableEntityAccessor) entity).isHighlighted()) {
					if (entity instanceof InteractionEntity interaction)
						SpecialHighlightRenderer.interaction(interaction, worldrendercontext);
					else
						SpecialHighlightRenderer.displayEntity((DisplayEntity) entity, worldrendercontext);
				}
			});
		});

		WorldRenderEvents.LAST.register((worldrendercontext) -> {
			CLIENT.world.getEntities().forEach((entity) -> {
				((HighlightableEntityAccessor) entity).setHighlighted(false);
			});
		});

		// Clear entity tags when disconnecting from server
		// Reset mod presence on server
		ClientPlayConnectionEvents.DISCONNECT.register((handler, client) -> {
            BetterSuggestionsModClient.ENTITY_TAGS.clear();
            BetterSuggestionsModClient.MOD_PRESENT_ON_SERVER = false;
        });

		FallbackTagGetter.init();
		ModPackets.initClient();
	}
}