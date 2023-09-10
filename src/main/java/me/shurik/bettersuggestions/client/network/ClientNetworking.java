package me.shurik.bettersuggestions.client.network;

import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.util.Identifier;

public class ClientNetworking {
    public static void send(Identifier packetId, PacketByteBuf buf) {
        ClientPlayNetworking.send(packetId, buf);
    }
}
