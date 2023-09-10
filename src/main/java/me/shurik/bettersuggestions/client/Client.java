package me.shurik.bettersuggestions.client;

import net.minecraft.client.MinecraftClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static me.shurik.bettersuggestions.ModConstants.MOD_ID;

public class Client {
    public static final Logger LOGGER = LoggerFactory.getLogger(MOD_ID + "-client");
    public static final MinecraftClient INSTANCE = MinecraftClient.getInstance();
    public static boolean escapePressed = false;
    /**
     * Whether the server has this mod installed.
     */
    public static boolean SERVER_SIDE_PRESENT = false;

    // TODO: make it configurable
    public static final long POLLING_INTERVAL = 3000L;
}