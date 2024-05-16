package me.shurik.bettersuggestions.network.packets;

import me.shurik.bettersuggestions.network.ModPackets;
import me.shurik.bettersuggestions.utils.ByteBufUtils;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.network.packet.CustomPayload;

import java.util.Set;

public record EntityCommandTagsResponseS2CPacket(int entityId, Set<String> commandTags) implements CustomPayload {
    public static final PacketCodec<PacketByteBuf, EntityCommandTagsResponseS2CPacket> CODEC
            = PacketCodec.of(EntityCommandTagsResponseS2CPacket::write, EntityCommandTagsResponseS2CPacket::new);

    public EntityCommandTagsResponseS2CPacket(PacketByteBuf buf) {
        this(buf.readInt(), ByteBufUtils.readSet(buf, PacketByteBuf::readString));
    }

    private void write(PacketByteBuf buf) {
        buf.writeInt(entityId);
        ByteBufUtils.writeCollection(buf, commandTags, PacketByteBuf::writeString);
    }

    @Override
    public Id<? extends CustomPayload> getId() {
        return ModPackets.EntityCommandTagsResponseS2CPacketID;
    }
}
