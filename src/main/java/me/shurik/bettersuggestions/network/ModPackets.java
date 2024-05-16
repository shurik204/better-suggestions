package me.shurik.bettersuggestions.network;

import me.shurik.bettersuggestions.network.packets.*;
import net.minecraft.network.packet.CustomPayload;
import net.minecraft.util.Identifier;

import static me.shurik.bettersuggestions.ModConstants.MOD_ID;
import static net.minecraft.network.packet.CustomPayload.*;

public class ModPackets {
    public static final Id<EntityCommandTagsRequestC2SPacket> EntityCommandTagsRequestC2SPacketID = CustomPayload.id(MOD_ID + "entity_tags_request");
    public static final Id<EntityCommandTagsResponseS2CPacket> EntityCommandTagsResponseS2CPacketID = CustomPayload.id(MOD_ID + "entity_tags_response");
    public static final Id<EntityScoresRequestC2SPacket> EntityScoresRequestC2SPacketID = CustomPayload.id(MOD_ID + "entity_scores_request");
    public static final Id<EntityScoresResponseS2CPacket> EntityScoresResponseS2CPacketID = CustomPayload.id(MOD_ID + "entity_scores_response");
    public static final Id<ModPresenceBeaconPacket> ModPresenceBeacon = CustomPayload.id(MOD_ID + "mod_presence");
}