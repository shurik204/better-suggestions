package me.shurik.bettersuggestions.mixin;

import me.shurik.bettersuggestions.access.SynchableEntityDataAccessor;
import me.shurik.bettersuggestions.data.TrackableTagSet;
import me.shurik.bettersuggestions.network.ServerPacketSender;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.Set;

@Mixin(Entity.class)
public abstract class EntityTagSyncMixin implements SynchableEntityDataAccessor {
    @Shadow
    private Set<String> commandTags;

    private boolean suggestions$dirty = false;

    @Inject(at = @At("RETURN"), method = "<init>")
    void init(EntityType<?> type, World world, CallbackInfo info) {
        // Replace the scoreboardTags Set with a custom one
        // that tracks changes and marks the entity for update if needed
        commandTags = new TrackableTagSet((Entity) (Object) this);
    }

    // public void onStartedTrackingBy(ServerPlayerEntity player)
    @Inject(at = @At("HEAD"), method = "onStartedTrackingBy")
    void onStartedTrackingBy(ServerPlayerEntity player, CallbackInfo info) {
        // If the entity is being tracked by a player for the first time,
        // send the current scoreboard tags to the client
        ServerPacketSender.sendEntityTagsUpdate(player, (Entity) (Object) this);
    }

    public boolean isDirty() {
        return suggestions$dirty;
    }

    public void setDirty() {
        suggestions$dirty = true;
    }

    public void setClean() {
        suggestions$dirty = false;
    }
}