package me.shurik.bettersuggestions.network.packets;

import me.shurik.bettersuggestions.network.ModPackets;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.network.packet.CustomPayload;

public record EntityCommandTagsRequestC2SPacket(int entityId) implements CustomPayload {
    public static final PacketCodec<PacketByteBuf, EntityCommandTagsRequestC2SPacket> CODEC
            = PacketCodec.of(EntityCommandTagsRequestC2SPacket::write, EntityCommandTagsRequestC2SPacket::new);

    public EntityCommandTagsRequestC2SPacket(PacketByteBuf buf) {
        this(buf.readInt());
    }

    private void write(PacketByteBuf buf) {
        buf.writeInt(entityId);
    }

    @Override
    public Id<? extends CustomPayload> getId() {
        return ModPackets.EntityCommandTagsRequestC2SPacketID;
    }
}
