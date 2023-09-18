package me.shurik.bettersuggestions.client.network;

import me.shurik.bettersuggestions.ModConstants;
import me.shurik.bettersuggestions.client.Client;
import me.shurik.bettersuggestions.client.access.ClientEntityDataAccessor;
import me.shurik.bettersuggestions.client.data.ClientDataGetter;
import me.shurik.bettersuggestions.network.ModPackets;
import me.shurik.bettersuggestions.utils.ByteBufUtils;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.fabricmc.fabric.api.networking.v1.PacketSender;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.ClientPlayNetworkHandler;
import net.minecraft.entity.Entity;
import net.minecraft.network.PacketByteBuf;

public class ClientPacketHandler {
    public static void init() {
        ClientPlayNetworking.registerGlobalReceiver(ModPackets.EntityCommandTagsS2CPacketResponseID, ClientPacketHandler::receiveEntityCommandTagsUpdate);
        ClientPlayNetworking.registerGlobalReceiver(ModPackets.EntityScoresS2CPacketResponseID, ClientPacketHandler::receiveEntityScoresUpdate);
    }

    private static void receiveEntityCommandTagsUpdate(MinecraftClient client, ClientPlayNetworkHandler handler, PacketByteBuf buf, PacketSender responseSender) {
        int entityId = buf.readInt();
        // Remove from pending requests
        ClientDataGetter.pendingTagRequests.remove(entityId);
        // Get entity
        Entity entity = client.world.getEntityById(entityId);
        if (entity == null) {
            if (ModConstants.DEBUG) {
                Client.LOGGER.warn("Received tag list for an unknown entity with ID " + entityId);
            }
            return;
        }

        ((ClientEntityDataAccessor) entity).setClientCommandTags(ByteBufUtils.readSet(buf, PacketByteBuf::readString));
    }

    private static void receiveEntityScoresUpdate(MinecraftClient client, ClientPlayNetworkHandler handler, PacketByteBuf buf, PacketSender responseSender) {
        int entityId = buf.readInt();
        // Remove from pending requests
        ClientDataGetter.pendingScoreRequests.remove(entityId);
        // Get entity
        Entity entity = client.world.getEntityById(entityId);
        if (entity == null) {
            if (ModConstants.DEBUG) {
                Client.LOGGER.warn("Received score list for an unknown entity with ID " + entityId);
            }
            return;
        }

        ((ClientEntityDataAccessor) entity).setClientScoreboardValues(ByteBufUtils.readSet(buf, ByteBufUtils::readScoreboardValue));
    }
}