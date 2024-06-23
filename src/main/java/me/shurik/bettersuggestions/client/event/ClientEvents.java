package me.shurik.bettersuggestions.client.event;

import me.shurik.bettersuggestions.client.Client;
import me.shurik.bettersuggestions.client.access.ClientEntityDataAccessor;
import me.shurik.bettersuggestions.client.render.SpecialRendererQueue;
import me.shurik.bettersuggestions.network.packet.ModPresenceBeaconPacket;
import net.fabricmc.fabric.api.client.networking.v1.C2SPlayChannelEvents;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayConnectionEvents;
import net.fabricmc.fabric.api.client.rendering.v1.WorldRenderEvents;

public class ClientEvents {
    public static void init() {
        // Special cases for rendering highlighted entities
        WorldRenderEvents.BEFORE_DEBUG_RENDER.register((worldrendercontext) -> {
            if (Client.INSTANCE.world != null) {
                SpecialRendererQueue.processQueue(worldrendercontext);
            }
//            SpecialRendererQueue.addBlock(new BlockPos(0,0,0));
        });

        // Clear entity highlight information after rendering
        WorldRenderEvents.LAST.register((worldrendercontext) -> {
            if (Client.INSTANCE.world != null) {
                Client.INSTANCE.world.getEntities().forEach((entity) -> ((ClientEntityDataAccessor) entity).setHighlighted(false));
            }
        });

        C2SPlayChannelEvents.REGISTER.register((handler, sender, client, channels) -> {
            if (Client.SERVER_SIDE_PRESENT) return;

            Client.SERVER_SIDE_PRESENT = channels.contains(ModPresenceBeaconPacket.ID.id());
            if (Client.SERVER_SIDE_PRESENT) {
                Client.LOGGER.info("Detected mod installed on server");
            }
        });

        // Clear entity tags when disconnecting from server
        // Reset mod presence on server
        ClientPlayConnectionEvents.DISCONNECT.register((handler, client) -> {
            Client.SERVER_SIDE_PRESENT = false;
            SpecialRendererQueue.clearAll();
            Client.storedChatCommand = null;
        });
    }
}