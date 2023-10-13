package me.shurik.bettersuggestions.network;

import me.shurik.bettersuggestions.utils.ByteBufUtils;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import me.shurik.bettersuggestions.mixin.EntityTrackerAccessor;
import me.shurik.bettersuggestions.mixin.ThreadedAnvilChunkStorageAccessor;
import net.minecraft.entity.Entity;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.scoreboard.ScoreboardPlayerScore;
import net.minecraft.server.network.PlayerAssociatedNetworkHandler;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerChunkManager;
import net.minecraft.server.world.ThreadedAnvilChunkStorage;
import net.minecraft.util.Identifier;
import net.minecraft.world.chunk.ChunkManager;

import java.util.Collection;
import java.util.Collections;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;

public class ServerNetworking {
    public static PacketByteBuf createEntityCommandTagsBuffer(int entityId, Set<String> commandTags) {
        return ByteBufUtils.writeCollection(ByteBufUtils.withInt(entityId), commandTags, PacketByteBuf::writeString);
    }

    public static PacketByteBuf createEntityScoresBuffer(int entityId, Collection<ScoreboardPlayerScore> scores) {
        return ByteBufUtils.writeCollection(ByteBufUtils.withInt(entityId), scores, ByteBufUtils::writeScoreboardValue);
    }

    // Copy of Fabric PlayerLookup.tracking
    public static Collection<ServerPlayerEntity> tracking(Entity entity) {
        Objects.requireNonNull(entity, "Entity cannot be null");
        ChunkManager manager = entity.getWorld().getChunkManager();

        if (manager instanceof ServerChunkManager) {
            ThreadedAnvilChunkStorage storage = ((ServerChunkManager) manager).threadedAnvilChunkStorage;
            EntityTrackerAccessor tracker = ((ThreadedAnvilChunkStorageAccessor) storage).getEntityTrackers().get(entity.getId());

            // return an immutable collection to guard against accidental removals.
            if (tracker != null) {
                return tracker.getPlayersTracking()
                        .stream().map(PlayerAssociatedNetworkHandler::getPlayer).collect(Collectors.toUnmodifiableSet());
            }

            return Collections.emptySet();
        }

        throw new IllegalArgumentException("Only supported on server worlds!");
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