package me.shurik.bettersuggestions.client.event;

import me.shurik.bettersuggestions.client.BetterSuggestionsModClient;
import me.shurik.bettersuggestions.client.access.HighlightableEntityAccessor;
import me.shurik.bettersuggestions.client.render.SpecialHighlightRenderer;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayConnectionEvents;
import net.fabricmc.fabric.api.client.rendering.v1.WorldRenderEvents;
import net.minecraft.client.MinecraftClient;
import net.minecraft.entity.decoration.DisplayEntity;
import net.minecraft.entity.decoration.InteractionEntity;


public class ClientEvents {
    private static final MinecraftClient client = MinecraftClient.getInstance();
    public static void init() {
        // Special cases for rendering highlighted entities
        WorldRenderEvents.BEFORE_DEBUG_RENDER.register((worldrendercontext) -> {
            if (client.world != null) {
                client.world.getEntities().forEach((entity) -> {
                    if ((entity instanceof InteractionEntity || entity instanceof DisplayEntity) && ((HighlightableEntityAccessor) entity).isHighlighted()) {
                        if (entity instanceof InteractionEntity interaction)
                            SpecialHighlightRenderer.interaction(interaction, worldrendercontext);
                        else
                            SpecialHighlightRenderer.displayEntity((DisplayEntity) entity, worldrendercontext);
                    }
                });
            }
        });

        // Clear entity highlight information after rendering
        WorldRenderEvents.LAST.register((worldrendercontext) -> {
            if (client.world != null) {
                client.world.getEntities().forEach((entity) -> ((HighlightableEntityAccessor) entity).setHighlighted(false));
            }
        });

        // Clear entity tags when disconnecting from server
        // Reset mod presence on server
        ClientPlayConnectionEvents.DISCONNECT.register((handler, client) -> {
            BetterSuggestionsModClient.ENTITY_TAGS.clear();
            BetterSuggestionsModClient.MOD_PRESENT_ON_SERVER = false;
        });
    }
}