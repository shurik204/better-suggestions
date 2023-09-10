package me.shurik.bettersuggestions.network;

import io.netty.buffer.ByteBufUtil;
import io.netty.buffer.Unpooled;
import me.shurik.bettersuggestions.mixin.EntityTrackerAccessor;
import me.shurik.bettersuggestions.mixin.ThreadedAnvilChunkStorageAccessor;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.entity.Entity;
import net.minecraft.network.PacketByteBuf;
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
        // Calculate the capacity of the buffer to avoid resizing
        // (4 bytes) [entity ID] + (4 bytes) [tag count] + sum(length of each tag)
        int capacity = 4 * 2 + commandTags.stream().mapToInt(ByteBufUtil::utf8Bytes).sum();
        PacketByteBuf buf = new PacketByteBuf(Unpooled.buffer(capacity, capacity)).writeVarInt(entityId).writeVarInt(commandTags.size());
        for (String tag : commandTags) buf.writeString(tag);

        return buf;
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