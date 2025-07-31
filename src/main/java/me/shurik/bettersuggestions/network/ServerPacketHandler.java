package me.shurik.bettersuggestions.network;

import me.shurik.bettersuggestions.ModConstants;
import me.shurik.bettersuggestions.network.packet.*;
import me.shurik.bettersuggestions.utils.Scoreboards;
import net.fabricmc.fabric.api.networking.v1.PayloadTypeRegistry;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.entity.Entity;
import net.minecraft.server.network.ServerPlayerEntity;

public class ServerPacketHandler {
    private static boolean hasPermissions(ServerPlayerEntity player) { return !ModConstants.CONFIG.server.requireOpToRequestData || player.hasPermissionLevel(1); }
    public static void init() {
        // One of the registrations is likely unnecessary
        PayloadTypeRegistry.playS2C().register(ModPresenceBeaconPacket.ID, ModPresenceBeaconPacket.CODEC);
        PayloadTypeRegistry.playC2S().register(ModPresenceBeaconPacket.ID, ModPresenceBeaconPacket.CODEC);
        ServerPlayNetworking.registerGlobalReceiver(ModPresenceBeaconPacket.ID, (packet, context) -> {
            // Do nothing
        });

        PayloadTypeRegistry.playC2S().register(EntityCommandTagsRequestC2SPacket.ID, EntityCommandTagsRequestC2SPacket.CODEC);
        ServerPlayNetworking.registerGlobalReceiver(EntityCommandTagsRequestC2SPacket.ID, ServerPacketHandler::receiveCommandTagsRequest);

        PayloadTypeRegistry.playC2S().register(EntityScoresRequestC2SPacket.ID, EntityScoresRequestC2SPacket.CODEC);
        ServerPlayNetworking.registerGlobalReceiver(EntityScoresRequestC2SPacket.ID, ServerPacketHandler::receiveScoresRequest);

        PayloadTypeRegistry.playS2C().register(EntityCommandTagsResponseS2CPacket.ID, EntityCommandTagsResponseS2CPacket.CODEC);
        PayloadTypeRegistry.playS2C().register(EntityScoresResponseS2CPacket.ID, EntityScoresResponseS2CPacket.CODEC);
    }

    private static void receiveCommandTagsRequest(EntityCommandTagsRequestC2SPacket packet, ServerPlayNetworking.Context context) {
        ServerPlayerEntity player = context.player();
        if (hasPermissions(player)) {
            int entityId = packet.entityId();
            Entity entity = player.getWorld().getEntityById(entityId);
            if (entity != null) {
                ServerPacketSender.sendEntityCommandTagsResponse(player, entityId, entity.getCommandTags());
            }
        }
    }

    private static void receiveScoresRequest(EntityScoresRequestC2SPacket packet, ServerPlayNetworking.Context context) {
        ServerPlayerEntity player = context.player();
        if (hasPermissions(player)) {
            int entityId = packet.entityId();
            Entity entity = player.getWorld().getEntityById(entityId);
            if (entity != null) {
                ServerPacketSender.sendEntityScoresResponse(player, entityId, Scoreboards.getScores(entity.getNameForScoreboard()));
            }
        }
    }
}