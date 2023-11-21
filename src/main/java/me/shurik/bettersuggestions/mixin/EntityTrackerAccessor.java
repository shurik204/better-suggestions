package me.shurik.bettersuggestions.mixin;

import net.minecraft.server.network.PlayerAssociatedNetworkHandler;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

import java.util.Set;

@Mixin(targets = "net/minecraft/server/world/ThreadedAnvilChunkStorage$EntityTracker")
public interface EntityTrackerAccessor {
    @Accessor("listeners")
    Set<PlayerAssociatedNetworkHandler> getPlayersTracking();
}