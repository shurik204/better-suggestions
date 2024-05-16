package me.shurik.bettersuggestions.network.packets;

import me.shurik.bettersuggestions.network.ModPackets;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.network.packet.CustomPayload;

public class ModPresenceBeaconPacket implements CustomPayload {
    public static final PacketCodec<PacketByteBuf, ModPresenceBeaconPacket> CODEC = PacketCodec.unit(new ModPresenceBeaconPacket());

    @Override
    public Id<? extends CustomPayload> getId() {
        return ModPackets.ModPresenceBeacon;
    }
}
