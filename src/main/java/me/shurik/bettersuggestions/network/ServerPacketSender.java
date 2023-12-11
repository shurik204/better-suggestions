package me.shurik.bettersuggestions.network;

import me.shurik.bettersuggestions.ModConstants;
import me.shurik.bettersuggestions.utils.Scoreboards.ScoreboardScoreContainer;
import net.minecraft.server.network.ServerPlayerEntity;

import java.util.Collection;
import java.util.Set;

public class ServerPacketSender {
    public static void sendEntityCommandTagsResponse(ServerPlayerEntity player, int entityId, Set<String> commandTags) {
        if (ModConstants.DEBUG) {
            ModConstants.LOGGER.info("Sending command tags for entity " + entityId + " (" + commandTags.size() + " tags)");
        }
        ServerNetworking.send(player, ModPackets.EntityCommandTagsS2CPacketResponseID, ServerNetworking.createEntityCommandTagsBuffer(entityId, commandTags));
    }

    public static void sendEntityScoresResponse(ServerPlayerEntity player, int entityId, Collection<ScoreboardScoreContainer> scores) {
        if (ModConstants.DEBUG) {
            ModConstants.LOGGER.info("Sending scores for entity " + entityId + " (" + scores.size() + " scores)");
        }
        ServerNetworking.send(player, ModPackets.EntityScoresS2CPacketResponseID, ServerNetworking.createEntityScoresBuffer(entityId, scores));
    }
}