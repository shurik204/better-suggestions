package me.shurik.bettersuggestions.client.network;

import me.shurik.bettersuggestions.ModConstants;
import me.shurik.bettersuggestions.client.Client;
import me.shurik.bettersuggestions.client.access.ClientEntityDataAccessor;
import me.shurik.bettersuggestions.client.data.ClientDataGetter;
import me.shurik.bettersuggestions.network.ModPackets;
import me.shurik.bettersuggestions.network.packets.EntityCommandTagsResponseS2CPacket;
import me.shurik.bettersuggestions.network.packets.EntityScoresResponseS2CPacket;
import me.shurik.bettersuggestions.utils.ByteBufUtils;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.fabricmc.fabric.api.networking.v1.PacketSender;
import net.fabricmc.fabric.api.networking.v1.PayloadTypeRegistry;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.ClientPlayNetworkHandler;
import net.minecraft.entity.Entity;
import net.minecraft.network.PacketByteBuf;

public class ClientPacketHandler {
    public static void init() {
        PayloadTypeRegistry.playS2C().register(ModPackets.EntityCommandTagsResponseS2CPacketID, EntityCommandTagsResponseS2CPacket.CODEC);
        ClientPlayNetworking.registerGlobalReceiver(ModPackets.EntityCommandTagsResponseS2CPacketID, ClientPacketHandler::receiveEntityCommandTagsUpdate);
        PayloadTypeRegistry.playS2C().register(ModPackets.EntityScoresResponseS2CPacketID, EntityScoresResponseS2CPacket.CODEC);
        ClientPlayNetworking.registerGlobalReceiver(ModPackets.EntityScoresResponseS2CPacketID, ClientPacketHandler::receiveEntityScoresUpdate);
    }

    private static void receiveEntityCommandTagsUpdate(EntityCommandTagsResponseS2CPacket packet, ClientPlayNetworking.Context context) {
        int entityId = packet.entityId();
        // Remove from pending requests
        ClientDataGetter.pendingTagRequests.remove(entityId);
        // Get entity
        Entity entity = context.client().world.getEntityById(entityId);
        if (entity == null) {
            if (ModConstants.DEBUG) {
                Client.LOGGER.warn("Received tag list for an unknown entity with ID " + entityId);
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
        Entity entity = context.client().world.getEntityById(entityId);
        if (entity == null) {
            if (ModConstants.DEBUG) {
                Client.LOGGER.warn("Received score list for an unknown entity with ID " + entityId);
            }
            return;
        }

        ((ClientEntityDataAccessor) entity).setClientScoreboardValues(packet.convertClientScoreboardValue());
    }
}