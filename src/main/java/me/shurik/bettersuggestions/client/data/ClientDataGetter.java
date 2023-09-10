package me.shurik.bettersuggestions.client.data;

import com.google.common.collect.Sets;
import me.shurik.bettersuggestions.ModConstants;
import me.shurik.bettersuggestions.client.Client;
import me.shurik.bettersuggestions.client.network.ClientPacketSender;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayConnectionEvents;
import net.minecraft.entity.Entity;
import net.minecraft.network.packet.c2s.play.RequestCommandCompletionsC2SPacket;

import java.util.Set;

import static me.shurik.bettersuggestions.client.Client.INSTANCE;

public class ClientDataGetter {
    public static final Set<Integer> pendingTagRequests = Sets.newHashSet();
    public static void init() {
        // Clear the queue on disconnect
        ClientPlayConnectionEvents.DISCONNECT.register((handler, client) -> pendingTagRequests.clear());
    }

    public static void requestEntityTags(Entity entity) {
        if (Client.SERVER_SIDE_PRESENT) {
            ClientPacketSender.sendEntityCommandTagsRequest(entity);
        } else {
            INSTANCE.getNetworkHandler().sendPacket(new RequestCommandCompletionsC2SPacket(-entity.getId() - 1_000_000_000, String.format("/tag %s remove ", entity.getUuidAsString())));
        }
        if (!pendingTagRequests.add(entity.getId()) && ModConstants.DEBUG) {
            Client.LOGGER.warn("Tags for entity " + entity.getId() + " (" + entity.getType().getName().toString() + ") were requested more than once!");
        }
    }
}