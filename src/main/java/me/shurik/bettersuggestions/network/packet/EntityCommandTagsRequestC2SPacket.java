package me.shurik.bettersuggestions.network.packet;

import me.shurik.bettersuggestions.BetterSuggestionsMod;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.network.packet.CustomPayload;

public record EntityCommandTagsRequestC2SPacket(int entityId) implements CustomPayload {
    public static final PacketCodec<PacketByteBuf, EntityCommandTagsRequestC2SPacket> CODEC
            = PacketCodec.of(EntityCommandTagsRequestC2SPacket::write, EntityCommandTagsRequestC2SPacket::new).cast();
    public static final Id<EntityCommandTagsRequestC2SPacket> ID = new Id<>(BetterSuggestionsMod.id("entity_tags_request"));

    public EntityCommandTagsRequestC2SPacket(PacketByteBuf buf) {
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