package me.shurik.bettersuggestions.network;

import net.minecraft.util.Identifier;

import static me.shurik.bettersuggestions.ModConstants.MOD_ID;

public class ModPackets {
    public static final Identifier EntityCommandTagsRequestC2SPacketID = new Identifier(MOD_ID, "entity_tags_request");
    public static final Identifier EntityCommandTagsS2CPacketResponseID = new Identifier(MOD_ID, "entity_tags_response");
    public static final Identifier EntityScoresRequestC2SPacketID = new Identifier(MOD_ID, "entity_scores_request");
    public static final Identifier EntityScoresS2CPacketResponseID = new Identifier(MOD_ID, "entity_scores_response");
    public static final Identifier ModPresenceBeacon = new Identifier(MOD_ID, "mod_presence");
}