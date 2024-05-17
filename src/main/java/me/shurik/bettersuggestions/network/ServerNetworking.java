package me.shurik.bettersuggestions.network;

import net.fabricmc.fabric.api.networking.v1.PlayerLookup;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.entity.Entity;
import net.minecraft.network.packet.CustomPayload;
import net.minecraft.server.network.ServerPlayerEntity;

import java.util.Collection;

public class ServerNetworking {
    // Copy of Fabric PlayerLookup.tracking
    public static Collection<ServerPlayerEntity> tracking(Entity entity) {
        return PlayerLookup.tracking(entity);
    }

    public static void broadcastFromEntity(Entity entity, CustomPayload packet) {
        for (ServerPlayerEntity player : tracking(entity)) {
            send(player, packet);
        }
    }

    public static void send(ServerPlayerEntity player, CustomPayload packet) {
        ServerPlayNetworking.send(player, packet);
    }
}