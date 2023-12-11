package me.shurik.bettersuggestions.network;

import me.shurik.bettersuggestions.ModConstants;
import me.shurik.bettersuggestions.utils.Scoreboards;
import net.fabricmc.fabric.api.networking.v1.PacketSender;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.entity.Entity;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.network.ServerPlayNetworkHandler;
import net.minecraft.server.network.ServerPlayerEntity;

public class ServerPacketHandler {
    private static boolean hasPermissions(ServerPlayerEntity player) { return !ModConstants.CONFIG.server.requireOpToRequestData || player.hasPermissionLevel(1); }
    public static void init() {
        ServerPlayNetworking.registerGlobalReceiver(ModPackets.ModPresenceBeacon, (server, player, handler, buf, responseSender) -> {
            // Do nothing
        });

        ServerPlayNetworking.registerGlobalReceiver(ModPackets.EntityCommandTagsRequestC2SPacketID, ServerPacketHandler::receiveCommandTagsRequest);
        ServerPlayNetworking.registerGlobalReceiver(ModPackets.EntityScoresRequestC2SPacketID, ServerPacketHandler::receiveScoresRequest);
    }

    private static void receiveCommandTagsRequest(MinecraftServer server, ServerPlayerEntity player, ServerPlayNetworkHandler handler, PacketByteBuf buf, PacketSender responseSender) {
        if (hasPermissions(player)) {
            int entityId = buf.readInt();
            Entity entity = player.getServerWorld().getEntityById(entityId);
            if (entity != null) {
                ServerPacketSender.sendEntityCommandTagsResponse(player, entityId, entity.getCommandTags());
            }
        }
    }

    private static void receiveScoresRequest(MinecraftServer server, ServerPlayerEntity player, ServerPlayNetworkHandler handler, PacketByteBuf buf, PacketSender responseSender) {
        if (hasPermissions(player)) {
            int entityId = buf.readInt();
            Entity entity = player.getServerWorld().getEntityById(entityId);
            if (entity != null) {
                ServerPacketSender.sendEntityScoresResponse(player, entityId, Scoreboards.getScores(entity.getNameForScoreboard()));
            }
        }
    }
}