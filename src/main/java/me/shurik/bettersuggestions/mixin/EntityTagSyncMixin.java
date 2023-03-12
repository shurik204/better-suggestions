package me.shurik.bettersuggestions.mixin;

import org.spongepowered.asm.mixin.injection.Inject;

import java.util.Set;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import com.google.common.collect.Sets;

import me.shurik.bettersuggestions.access.SynchableEntityDataAccessor;
import me.shurik.bettersuggestions.data.TrackableTagSet;
import me.shurik.bettersuggestions.networking.ModPackets;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.world.World;

@Mixin(Entity.class)
public abstract class EntityTagSyncMixin implements SynchableEntityDataAccessor {
    @Shadow
    private Set<String> scoreboardTags;

    private boolean suggestions$dirty = false;

    private Set<String> clientScoreboardTags;

    @Inject(at = @At("RETURN"), method = "<init>")
    void init(EntityType<?> type, World world, CallbackInfo info) {
        // Replace the scoreboardTags Set with a custom one
        // that tracks changes and marks the entity for update if needed
        scoreboardTags = new TrackableTagSet((Entity) (Object) this);
        clientScoreboardTags = Sets.newHashSet();
    }

    
    // public void onStartedTrackingBy(ServerPlayerEntity player)
    @Inject(at = @At("HEAD"), method = "onStartedTrackingBy")
    void onStartedTrackingBy(ServerPlayerEntity player, CallbackInfo info) {
        // If the entity is being tracked by a player for the first time,
        // send the current scoreboard tags to the client
        ModPackets.EntityTagsUpdateS2CPacket.send(player, (Entity) (Object) this);
    }

    public Set<String> getClientTags() {
        return clientScoreboardTags;
    }

    public void setClientTags(Set<String> tags) {
        clientScoreboardTags = tags;
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