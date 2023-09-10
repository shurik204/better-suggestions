package me.shurik.bettersuggestions.network;

import me.shurik.bettersuggestions.ModConstants;
import net.fabricmc.fabric.api.networking.v1.ServerPlayConnectionEvents;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.entity.Entity;

public class ServerPacketHandler {
    public static void init() {
        ServerPlayNetworking.registerGlobalReceiver(ModPackets.ModPresenceBeacon, (server, player, handler, buf, responseSender) -> {
            // Do nothing
        });

        ServerPlayNetworking.registerGlobalReceiver(ModPackets.EntityCommandTagsRequestC2SPacketID, (server, player, handler, buf, responseSender) -> {
            if (ModConstants.CONFIG.server.requireOpToRequestData && !player.hasPermissionLevel(1)) return;

            int entityId = buf.readInt();
            Entity entity = player.getServerWorld().getEntityById(entityId);
            if (entity != null) {
                ServerPacketSender.sendEntityCommandTagsResponse(player, entityId, entity.getCommandTags());
            }
        });

        ServerPlayConnectionEvents.JOIN.register((handler, sender, server) -> {
            ServerPacketSender.sendModPresence(handler.player);
        });
    }
}