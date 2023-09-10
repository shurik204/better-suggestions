package me.shurik.bettersuggestions.network;

import me.shurik.bettersuggestions.ModConstants;
import me.shurik.bettersuggestions.utils.ByteBufUtils;
import net.minecraft.server.network.ServerPlayerEntity;

import java.util.Set;

public class ServerPacketSender {
    public static void sendModPresence(ServerPlayerEntity player) {
         ServerNetworking.send(player, ModPackets.ModPresenceBeacon, ByteBufUtils.empty());
    }

    public static void sendEntityCommandTagsResponse(ServerPlayerEntity player, int entityId, Set<String> commandTags) {
        if (ModConstants.DEBUG) {
            ModConstants.LOGGER.info("Sending command tags for entity " + entityId + " (" + commandTags.size() + " tags)");
        }
        ServerNetworking.send(player, ModPackets.EntityCommandTagsS2CPacketResponseID, ServerNetworking.createEntityCommandTagsBuffer(entityId, commandTags));
    }
}