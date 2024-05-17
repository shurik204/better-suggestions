package me.shurik.bettersuggestions.network.packet;

import me.shurik.bettersuggestions.BetterSuggestionsMod;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.network.packet.CustomPayload;

public record EntityScoresRequestC2SPacket(int entityId) implements CustomPayload {
    public static final PacketCodec<PacketByteBuf, EntityScoresRequestC2SPacket> CODEC
            = PacketCodec.of(EntityScoresRequestC2SPacket::write, EntityScoresRequestC2SPacket::new).cast();
    public static final Id<EntityScoresRequestC2SPacket> ID = new Id<>(BetterSuggestionsMod.id("entity_scores_request"));

    public EntityScoresRequestC2SPacket(PacketByteBuf buf) {
        this(buf.readInt());
    }

    private void write(PacketByteBuf buf) {
        buf.writeInt(entityId);
    }

    @Override
    public Id<? extends CustomPayload> getId() {
        return ID;
    }
}
