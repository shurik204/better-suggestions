package me.shurik.bettersuggestions.event;

import me.shurik.bettersuggestions.Server;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerLifecycleEvents;

public class ServerEvents {
    public static void init() {
		ServerLifecycleEvents.SERVER_STARTING.register((server) -> { Server.INSTANCE = server; });
    }
}