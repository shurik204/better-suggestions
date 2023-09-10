package me.shurik.bettersuggestions.event;

import me.shurik.bettersuggestions.Server;
import me.shurik.bettersuggestions.network.ServerPacketSender;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerLifecycleEvents;
import net.fabricmc.fabric.api.networking.v1.ServerPlayConnectionEvents;

public class ServerEvents {
    public static void init() {
		// Check for entity tag updates and send packets to clients
//        ServerTickEvents.END_WORLD_TICK.register((world) -> {
//			world.getEntityLookup().iterate().forEach((entity) -> {
//				SynchableEntityDataAccessor accessor = (SynchableEntityDataAccessor) entity;
//				if (accessor.isDirty()) {
//					ServerPacketSender.broadcastEntityTagsUpdate(entity);
//					accessor.setClean();
//				}
//			});
//		});

		ServerLifecycleEvents.SERVER_STARTING.register((server) -> { Server.INSTANCE = server; });
		ServerPlayConnectionEvents.JOIN.register((handler, sender, server) -> { ServerPacketSender.sendModPresence(handler.player); });
    }
}