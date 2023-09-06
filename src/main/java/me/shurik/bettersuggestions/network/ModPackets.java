package me.shurik.bettersuggestions.network;

import net.minecraft.util.Identifier;

public class ModPackets {
    public static final Identifier ScoreboardUpdateS2CPacketID = new Identifier("bettersuggestions", "scoreboard_update");
    public static final Identifier EntityTagsUpdateS2CPacketID = new Identifier("bettersuggestions", "entity_tags_update");
    public static final Identifier ModPresenceS2CPacketID = new Identifier("bettersuggestions", "mod_presence");
}