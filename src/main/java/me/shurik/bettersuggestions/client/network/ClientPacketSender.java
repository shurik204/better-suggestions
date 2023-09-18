package me.shurik.bettersuggestions.client.network;

import me.shurik.bettersuggestions.network.ModPackets;
import me.shurik.bettersuggestions.utils.ByteBufUtils;
import net.minecraft.entity.Entity;

public class ClientPacketSender {
    public static void sendEntityCommandTagsRequest(Entity entity) { sendEntityCommandTagsRequest(entity.getId()); }
    public static void sendEntityCommandTagsRequest(int entityId) { ClientNetworking.send(ModPackets.EntityCommandTagsRequestC2SPacketID, ByteBufUtils.withInt(entityId)); }
    public static void sendEntityScoresRequest(Entity entity) { sendEntityScoresRequest(entity.getId()); }
    public static void sendEntityScoresRequest(int entityId) { ClientNetworking.send(ModPackets.EntityScoresRequestC2SPacketID, ByteBufUtils.withInt(entityId)); }
}