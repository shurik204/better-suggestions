package me.shurik.bettersuggestions.networking;

import java.util.HashSet;
import java.util.Set;

import me.shurik.bettersuggestions.BetterSuggestionsModClient;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.fabricmc.fabric.api.networking.v1.PacketByteBufs;
import net.fabricmc.fabric.api.networking.v1.PacketSender;
import net.fabricmc.fabric.api.networking.v1.PlayerLookup;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.ClientPlayNetworkHandler;
import net.minecraft.entity.Entity;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.Identifier;

public class ModPackets {
    public class EntityTagsUpdateS2CPacket {
        public static final Identifier ID = new Identifier("bettersuggestions", "entity_tags_update");

        public static void register() {
            ClientPlayNetworking.registerGlobalReceiver(ID, EntityTagsUpdateS2CPacket::handle);
        }

        public static void send(ServerPlayerEntity player, Entity entity) {
            PacketByteBuf buf = PacketByteBufs.create();
            buf.writeVarInt(entity.getId());
            buf.writeVarInt(entity.getCommandTags().size());
            for (String tag : entity.getCommandTags()) {
                buf.writeString(tag);
            }
            ServerPlayNetworking.send(player, ID, buf);
        }

        public static void broadcastFromEntity(Entity entity) {
            for (ServerPlayerEntity player : PlayerLookup.tracking((ServerWorld) entity.getWorld(), entity.getBlockPos())) {
                EntityTagsUpdateS2CPacket.send(player, entity);
            }
        }

        public static void handle(MinecraftClient client, ClientPlayNetworkHandler handler, PacketByteBuf buf, PacketSender responseSender) {
            int entityId = buf.readVarInt();
            int tagCount = buf.readVarInt();
            Set<String> tags = new HashSet<>();
            for (int i = 0; i < tagCount; i++) {
                tags.add(buf.readString());
            }

            BetterSuggestionsModClient.ENTITY_TAGS.put(entityId, tags);
        }
    }

    public static void init() {
        
    }

    public static void initClient() {
        EntityTagsUpdateS2CPacket.register();
    }
}