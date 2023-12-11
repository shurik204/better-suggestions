package me.shurik.bettersuggestions.network;

import me.shurik.bettersuggestions.utils.ByteBufUtils;
import me.shurik.bettersuggestions.utils.Scoreboards.ScoreboardScoreContainer;
import net.fabricmc.fabric.api.networking.v1.PlayerLookup;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.entity.Entity;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.Identifier;

import java.util.Collection;
import java.util.Set;

public class ServerNetworking {
    public static PacketByteBuf createEntityCommandTagsBuffer(int entityId, Set<String> commandTags) {
        return ByteBufUtils.writeCollection(ByteBufUtils.withInt(entityId), commandTags, PacketByteBuf::writeString);
    }

    public static PacketByteBuf createEntityScoresBuffer(int entityId, Collection<ScoreboardScoreContainer> scores) {
        return ByteBufUtils.writeCollection(ByteBufUtils.withInt(entityId), scores, ByteBufUtils::writeScoreboardValue);
    }

    // Copy of Fabric PlayerLookup.tracking
    public static Collection<ServerPlayerEntity> tracking(Entity entity) {
        return PlayerLookup.tracking(entity);
    }

    public static void broadcastFromEntity(Entity entity, Identifier packetId, PacketByteBuf buf) {
        for (ServerPlayerEntity player : tracking(entity)) {
            send(player, packetId, buf);
        }
    }

    public static void send(ServerPlayerEntity player, Identifier packetId, PacketByteBuf buf) {
        ServerPlayNetworking.send(player, packetId, buf);
    }
}