package me.shurik.bettersuggestions.network;

import me.shurik.bettersuggestions.ModConstants;
import me.shurik.bettersuggestions.interfaces.ScoreboardValue;
import me.shurik.bettersuggestions.network.packets.EntityCommandTagsResponseS2CPacket;
import me.shurik.bettersuggestions.network.packets.EntityScoresResponseS2CPacket;
import me.shurik.bettersuggestions.utils.Scoreboards.ScoreboardScoreContainer;
import net.minecraft.server.network.ServerPlayerEntity;

import java.util.Collection;
import java.util.Set;

public class ServerPacketSender {
    public static void sendEntityCommandTagsResponse(ServerPlayerEntity player, int entityId, Set<String> commandTags) {
        if (ModConstants.DEBUG) {
            ModConstants.LOGGER.info("Sending command tags for entity " + entityId + " (" + commandTags.size() + " tags)");
        }
        ServerNetworking.send(player, new EntityCommandTagsResponseS2CPacket(entityId, commandTags));
    }

    public static void sendEntityScoresResponse(ServerPlayerEntity player, int entityId, Collection<? extends ScoreboardValue> scores) {
        if (ModConstants.DEBUG) {
            ModConstants.LOGGER.info("Sending scores for entity " + entityId + " (" + scores.size() + " scores)");
        }
        ServerNetworking.send(player, new EntityScoresResponseS2CPacket(entityId, scores));
    }
}