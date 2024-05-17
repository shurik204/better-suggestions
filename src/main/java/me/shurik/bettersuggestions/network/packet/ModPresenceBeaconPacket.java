package me.shurik.bettersuggestions.network.packet;

import me.shurik.bettersuggestions.BetterSuggestionsMod;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.network.packet.CustomPayload;

public class ModPresenceBeaconPacket implements CustomPayload {
    public static final PacketCodec<PacketByteBuf, ModPresenceBeaconPacket> CODEC = PacketCodec.unit(new ModPresenceBeaconPacket());
    public static final Id<ModPresenceBeaconPacket> ID = new Id<>(BetterSuggestionsMod.id("mod_presence_beacon"));

    @Override
    public Id<? extends CustomPayload> getId() {
        return ID;
    }
}
