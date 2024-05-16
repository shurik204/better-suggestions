package me.shurik.bettersuggestions.network;

import me.shurik.bettersuggestions.ModConstants;
import me.shurik.bettersuggestions.network.packets.EntityCommandTagsRequestC2SPacket;
import me.shurik.bettersuggestions.network.packets.EntityScoresRequestC2SPacket;
import me.shurik.bettersuggestions.network.packets.ModPresenceBeaconPacket;
import me.shurik.bettersuggestions.utils.Scoreboards;
import net.fabricmc.fabric.api.networking.v1.PayloadTypeRegistry;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.entity.Entity;
import net.minecraft.server.network.ServerPlayerEntity;

public class ServerPacketHandler {
    private static boolean hasPermissions(ServerPlayerEntity player) { return !ModConstants.CONFIG.server.requireOpToRequestData || player.hasPermissionLevel(1); }
    public static void init() {
        PayloadTypeRegistry.playC2S().register(ModPackets.ModPresenceBeacon, ModPresenceBeaconPacket.CODEC);
        ServerPlayNetworking.registerGlobalReceiver(ModPackets.ModPresenceBeacon, (packet, context) -> {
            // Do nothing
        });

        PayloadTypeRegistry.playC2S().register(ModPackets.EntityCommandTagsRequestC2SPacketID, EntityCommandTagsRequestC2SPacket.CODEC);
        ServerPlayNetworking.registerGlobalReceiver(ModPackets.EntityCommandTagsRequestC2SPacketID, ServerPacketHandler::receiveCommandTagsRequest);
        PayloadTypeRegistry.playC2S().register(ModPackets.EntityScoresRequestC2SPacketID, EntityScoresRequestC2SPacket.CODEC);
        ServerPlayNetworking.registerGlobalReceiver(ModPackets.EntityScoresRequestC2SPacketID, ServerPacketHandler::receiveScoresRequest);
    }

    private static void receiveCommandTagsRequest(EntityCommandTagsRequestC2SPacket packet, ServerPlayNetworking.Context context) {
        ServerPlayerEntity player = context.player();
        if (hasPermissions(player)) {
            int entityId = packet.entityId();
            Entity entity = player.getServerWorld().getEntityById(entityId);
            if (entity != null) {
                ServerPacketSender.sendEntityCommandTagsResponse(player, entityId, entity.getCommandTags());
            }
        }
    }

    private static void receiveScoresRequest(EntityScoresRequestC2SPacket packet, ServerPlayNetworking.Context context) {
        ServerPlayerEntity player = context.player();
        if (hasPermissions(player)) {
            int entityId = packet.entityId();
            Entity entity = player.getServerWorld().getEntityById(entityId);
            if (entity != null) {
                ServerPacketSender.sendEntityScoresResponse(player, entityId, Scoreboards.getScores(entity.getNameForScoreboard()));
            }
        }
    }
}