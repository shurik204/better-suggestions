package me.shurik.bettersuggestions.network;

import net.fabricmc.fabric.api.networking.v1.PacketByteBufs;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.entity.Entity;
import net.minecraft.server.network.ServerPlayerEntity;

public class ServerPacketSender {
    public static void sendEntityTagsUpdate(ServerPlayerEntity player, Entity entity) {
        ServerPlayNetworking.send(player, ModPackets.EntityTagsUpdateS2CPacketID, NetworkUtils.createEntityTagsBuffer(entity));
    }

    public static void broadcastEntityTagsUpdate(Entity entity) {
        NetworkUtils.broadcastFromEntity(entity, ModPackets.EntityTagsUpdateS2CPacketID, NetworkUtils.createEntityTagsBuffer(entity));
    }

    public static void sendModPresence(ServerPlayerEntity player) {
        ServerPlayNetworking.send(player, ModPackets.ModPresenceS2CPacketID, PacketByteBufs.empty());
    }
}