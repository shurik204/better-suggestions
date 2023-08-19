package me.shurik.bettersuggestions.network;

import net.fabricmc.fabric.api.networking.v1.PacketByteBufs;
import net.fabricmc.fabric.api.networking.v1.PlayerLookup;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.entity.Entity;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.Identifier;

public class NetworkUtils {
    public static PacketByteBuf createEntityTagsBuffer(Entity entity) {
        // Entity ID, tag count, tag list (strings)
        PacketByteBuf buf = PacketByteBufs.create().writeVarInt(entity.getId()).writeVarInt(entity.getCommandTags().size());
        for (String tag : entity.getCommandTags()) buf.writeString(tag);

        return buf;
    }

    public static void broadcastFromEntity(Entity entity, Identifier packetId, PacketByteBuf buf) {
        for (ServerPlayerEntity player : PlayerLookup.tracking((ServerWorld) entity.getWorld(), entity.getBlockPos())) {
            ServerPlayNetworking.send(player, packetId, buf);
        }
    }
}