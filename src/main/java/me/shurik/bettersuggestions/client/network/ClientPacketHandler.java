package me.shurik.bettersuggestions.client.network;

import me.shurik.bettersuggestions.ModConstants;
import me.shurik.bettersuggestions.client.Client;
import me.shurik.bettersuggestions.client.access.ClientEntityDataAccessor;
import me.shurik.bettersuggestions.client.data.ClientDataGetter;
import me.shurik.bettersuggestions.network.packet.EntityCommandTagsResponseS2CPacket;
import me.shurik.bettersuggestions.network.packet.EntityScoresResponseS2CPacket;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.minecraft.entity.Entity;

import java.util.Objects;

public class ClientPacketHandler {
    public static void init() {
        ClientPlayNetworking.registerGlobalReceiver(EntityCommandTagsResponseS2CPacket.ID, ClientPacketHandler::receiveEntityCommandTagsUpdate);
        ClientPlayNetworking.registerGlobalReceiver(EntityScoresResponseS2CPacket.ID, ClientPacketHandler::receiveEntityScoresUpdate);
    }

    private static void receiveEntityCommandTagsUpdate(EntityCommandTagsResponseS2CPacket packet, ClientPlayNetworking.Context context) {
        int entityId = packet.entityId();
        // Remove from pending requests
        ClientDataGetter.pendingTagRequests.remove(entityId);
        // Get entity
        Entity entity = Objects.requireNonNull(context.client().world).getEntityById(entityId);
        if (entity == null) {
            if (ModConstants.DEBUG) {
                Client.LOGGER.warn("Received tag list for an unknown entity with ID {}", entityId);
            }
            return;
        }

        ((ClientEntityDataAccessor) entity).setClientCommandTags(packet.commandTags());
    }

    private static void receiveEntityScoresUpdate(EntityScoresResponseS2CPacket packet, ClientPlayNetworking.Context context) {
        int entityId = packet.entityId();
        // Remove from pending requests
        ClientDataGetter.pendingScoreRequests.remove(entityId);
        // Get entity
        Entity entity = Objects.requireNonNull(context.client().world).getEntityById(entityId);
        if (entity == null) {
            if (ModConstants.DEBUG) {
                Client.LOGGER.warn("Received score list for an unknown entity with ID {}", entityId);
            }
            return;
        }

        ((ClientEntityDataAccessor) entity).setClientScoreboardValues(packet.convertClientScoreboardValue());
    }
}