package me.shurik.bettersuggestions.network.packets;

import me.shurik.bettersuggestions.network.ModPackets;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.network.packet.CustomPayload;

public record EntityScoresRequestC2SPacket(int entityId) implements CustomPayload {
    public static final PacketCodec<PacketByteBuf, EntityScoresRequestC2SPacket> CODEC
            = PacketCodec.of(EntityScoresRequestC2SPacket::write, EntityScoresRequestC2SPacket::new);

    public EntityScoresRequestC2SPacket(PacketByteBuf buf) {
        this(buf.readInt());
    }

    private void write(PacketByteBuf buf) {
        buf.writeInt(entityId);
    }

    @Override
    public Id<? extends CustomPayload> getId() {
        return ModPackets.EntityScoresRequestC2SPacketID;
    }
}
