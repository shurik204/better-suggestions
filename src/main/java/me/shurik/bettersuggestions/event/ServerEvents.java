package me.shurik.bettersuggestions.event;

import me.shurik.bettersuggestions.access.SynchableEntityDataAccessor;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerTickEvents;

import me.shurik.bettersuggestions.networking.ModPackets.EntityTagsUpdateS2CPacket;

public class ServerEvents {
    public static void init() {
        ServerTickEvents.END_WORLD_TICK.register((world) -> {
			world.getEntityLookup().iterate().forEach((entity) -> {
				SynchableEntityDataAccessor accessor = (SynchableEntityDataAccessor) entity;
				if (accessor.isDirty()) {
					EntityTagsUpdateS2CPacket.broadcastFromEntity(entity);
					accessor.setClean();
				}
			});
		});
    }
}
