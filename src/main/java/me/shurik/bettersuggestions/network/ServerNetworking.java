package me.shurik.bettersuggestions.network;

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
import java.util.stream.Collectors;

public class ServerNetworking {
    public static final PacketByteBuf EMPTY_BUFFER = new PacketByteBuf(Unpooled.EMPTY_BUFFER);
    public static PacketByteBuf createEntityTagsBuffer(Entity entity) {
        // Entity ID, tag count, tag list (strings)
        PacketByteBuf buf = new PacketByteBuf(Unpooled.buffer()).writeVarInt(entity.getId()).writeVarInt(entity.getCommandTags().size());
        for (String tag : entity.getCommandTags()) buf.writeString(tag);

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
            sendPacketToClient(player, packetId, buf);
        }
    }

    public static void sendPacketToClient(ServerPlayerEntity player, Identifier packetId, PacketByteBuf buf) {
        ServerPlayNetworking.send(player, packetId, buf);
    }
}