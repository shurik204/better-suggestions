package me.shurik.bettersuggestions.utils;

import net.fabricmc.api.Environment;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayConnectionEvents;
import net.minecraft.entity.Entity;
import net.minecraft.network.packet.c2s.play.RequestCommandCompletionsC2SPacket;

import static me.shurik.bettersuggestions.BetterSuggestionsModClient.MOD_PRESENT_ON_SERVER;
import static me.shurik.bettersuggestions.BetterSuggestionsModClient.CLIENT;

import java.util.Set;

import com.google.common.collect.Sets;

import net.fabricmc.api.EnvType;

@Environment(EnvType.CLIENT)
public class FallbackTagGetter {
    public static long lastResetTime;
    public static final Set<Integer> recentTagRequests = Sets.newHashSet();

    public static void init() {
        ClientTickEvents.END_CLIENT_TICK.register((client) -> {
            // Reset recent tag requests every 5 seconds
			if (client.world != null && !MOD_PRESENT_ON_SERVER && System.currentTimeMillis() - lastResetTime > 5000) {
                recentTagRequests.clear();
                lastResetTime = System.currentTimeMillis();
			}
		});

        ClientPlayConnectionEvents.DISCONNECT.register((handler, client) -> {
            FallbackTagGetter.recentTagRequests.clear();
        });
    }

    public static void tryRequestEntityTags(Entity entity) {
        if (!recentTagRequests.contains(entity.getId())) {
            CLIENT.getNetworkHandler().sendPacket(new RequestCommandCompletionsC2SPacket(-entity.getId() - 1_000_000_000, String.format("/tag %s remove ", entity.getUuidAsString())));
            recentTagRequests.add(entity.getId());
        }
    }
}