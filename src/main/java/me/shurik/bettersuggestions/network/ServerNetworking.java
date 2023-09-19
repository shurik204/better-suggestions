package me.shurik.bettersuggestions.network;

import me.shurik.bettersuggestions.mixin.EntityTrackerAccessor;
import me.shurik.bettersuggestions.mixin.ThreadedAnvilChunkStorageAccessor;
import me.shurik.bettersuggestions.utils.ByteBufUtils;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.entity.Entity;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.scoreboard.ScoreboardPlayerScore;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.EntityTrackingListener;
import net.minecraft.server.world.ServerChunkManager;
import net.minecraft.server.world.ThreadedAnvilChunkStorage;
import net.minecraft.util.Identifier;
import net.minecraft.world.chunk.ChunkManager;

import java.util.Collection;
import java.util.Collections;
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
        ChunkManager manager = entity.getWorld().getChunkManager();

        if (manager instanceof ServerChunkManager) {
            ThreadedAnvilChunkStorage storage = ((ServerChunkManager) manager).threadedAnvilChunkStorage;
            EntityTrackerAccessor tracker = ((ThreadedAnvilChunkStorageAccessor) storage).getEntityTrackers().get(entity.getId());

            // return an immutable collection to guard against accidental removals.
            if (tracker != null) {
                return tracker.getPlayersTracking().stream().map(EntityTrackingListener::getPlayer).collect(Collectors.toUnmodifiableSet());
            }
        }

        return Collections.emptySet();
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