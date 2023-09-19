package me.shurik.bettersuggestions.event;

import net.fabricmc.fabric.api.event.Event;
import net.fabricmc.fabric.api.event.EventFactory;
import net.minecraft.server.MinecraftServer;

/**
 * Copy of ServerLifecycleEvents from Fabric API.
 */
public class ServerEvents {
    /**
     * Called before a Minecraft server reloads data packs.
     */
    public static final Event<StartDataPackReload> START_DATA_PACK_RELOAD = EventFactory.createArrayBacked(StartDataPackReload.class, callbacks -> (server) -> {
        for (StartDataPackReload callback : callbacks) {
            callback.startDataPackReload(server);
        }
    });

    @FunctionalInterface
    public interface StartDataPackReload {
        void startDataPackReload(MinecraftServer server);
    }
}
