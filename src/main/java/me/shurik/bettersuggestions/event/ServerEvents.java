package me.shurik.bettersuggestions.event;

import me.shurik.bettersuggestions.access.SynchableEntityDataAccessor;
import me.shurik.bettersuggestions.network.ServerPacketSender;
import me.shurik.bettersuggestions.utils.Scoreboards;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerLifecycleEvents;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerTickEvents;
import net.fabricmc.fabric.api.networking.v1.ServerPlayConnectionEvents;

public class ServerEvents {
    public static void init() {
		// Check for entity tag updates and send packets to clients
        ServerTickEvents.END_WORLD_TICK.register((world) -> {
			world.getEntityLookup().iterate().forEach((entity) -> {
				SynchableEntityDataAccessor accessor = (SynchableEntityDataAccessor) entity;
				if (accessor.isDirty()) {
					ServerPacketSender.broadcastEntityTagsUpdate(entity);
					accessor.setClean();
				}
			});
		});

		ServerLifecycleEvents.SERVER_STARTING.register((server) -> {
			Scoreboards.server = server;
		});

		// Send mod presence packet to client on join
		ServerPlayConnectionEvents.JOIN.register((handler, sender, server) -> ServerPacketSender.sendModPresence(handler.player));
    }
}