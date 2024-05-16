package me.shurik.bettersuggestions.network.packets;

import me.shurik.bettersuggestions.client.data.ClientScoreboardValue;
import me.shurik.bettersuggestions.interfaces.ScoreboardValue;
import me.shurik.bettersuggestions.network.ModPackets;
import me.shurik.bettersuggestions.utils.ByteBufUtils;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.network.packet.CustomPayload;

import java.util.Collection;
import java.util.Set;
import java.util.stream.Collectors;

public record EntityScoresResponseS2CPacket(int entityId, Collection<? extends ScoreboardValue> scores) implements CustomPayload {
    public static final PacketCodec<PacketByteBuf, EntityScoresResponseS2CPacket> CODEC
            = PacketCodec.of(EntityScoresResponseS2CPacket::write, EntityScoresResponseS2CPacket::new);

    public EntityScoresResponseS2CPacket(PacketByteBuf buf) {
        this(buf.readInt(), ByteBufUtils.readCollection(buf, ByteBufUtils::readScoreboardValue));
    }

    private void write(PacketByteBuf buf) {
        buf.writeInt(entityId);
        ByteBufUtils.writeCollection(buf, scores, ByteBufUtils::writeScoreboardValue);
    }

    @Environment(EnvType.CLIENT)
    public Set<ClientScoreboardValue> convertClientScoreboardValue() {
        return scores.stream().map(sv -> (ClientScoreboardValue) sv).collect(Collectors.toSet());
    }

    @Override
    public Id<? extends CustomPayload> getId() {
        return ModPackets.EntityScoresResponseS2CPacketID;
    }
}
