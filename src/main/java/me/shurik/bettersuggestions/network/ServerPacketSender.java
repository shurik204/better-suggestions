package me.shurik.bettersuggestions.network;

import net.minecraft.entity.Entity;
import net.minecraft.server.network.ServerPlayerEntity;

public class ServerPacketSender {
    public static void sendEntityTagsUpdate(ServerPlayerEntity player, Entity entity) {
        ServerNetworking.sendPacketToClient(player, ModPackets.EntityTagsUpdateS2CPacketID, ServerNetworking.createEntityTagsBuffer(entity));
    }

    public static void broadcastEntityTagsUpdate(Entity entity) {
        ServerNetworking.broadcastFromEntity(entity, ModPackets.EntityTagsUpdateS2CPacketID, ServerNetworking.createEntityTagsBuffer(entity));
    }

    public static void sendModPresence(ServerPlayerEntity player) {
        ServerNetworking.sendPacketToClient(player, ModPackets.ModPresenceS2CPacketID, ServerNetworking.EMPTY_BUFFER);
    }
}