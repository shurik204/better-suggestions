package me.shurik.bettersuggestions.client.network;

import me.shurik.bettersuggestions.client.BetterSuggestionsModClient;
import me.shurik.bettersuggestions.network.ModPackets;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.fabricmc.fabric.api.networking.v1.PacketSender;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.ClientPlayNetworkHandler;
import net.minecraft.network.PacketByteBuf;

import java.util.HashSet;
import java.util.Set;

public class ClientPacketHandler {
    public static void init() {
        ClientPlayNetworking.registerGlobalReceiver(ModPackets.EntityTagsUpdateS2CPacketID, ClientPacketHandler::receiveTagListUpdate);
        ClientPlayNetworking.registerGlobalReceiver(ModPackets.ModPresenceS2CPacketID, ClientPacketHandler::receiveServerModPresence);
    }

    private static void receiveTagListUpdate(MinecraftClient client, ClientPlayNetworkHandler handler, PacketByteBuf buf, PacketSender responseSender) {
        int entityId = buf.readVarInt();
        int tagCount = buf.readVarInt();
        Set<String> tags = new HashSet<>();
        for (int i = 0; i < tagCount; i++) {
            tags.add(buf.readString());
        }

        BetterSuggestionsModClient.ENTITY_TAGS.put(entityId, tags);
    }

    private static void receiveServerModPresence(MinecraftClient client, ClientPlayNetworkHandler handler, PacketByteBuf buf, PacketSender responseSender) {
        BetterSuggestionsModClient.MOD_PRESENT_ON_SERVER = true;
    }
}
