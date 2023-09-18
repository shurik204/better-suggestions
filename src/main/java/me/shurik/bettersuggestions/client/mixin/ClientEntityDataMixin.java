package me.shurik.bettersuggestions.client.mixin;

import me.shurik.bettersuggestions.client.Client;
import me.shurik.bettersuggestions.client.access.ClientEntityDataAccessor;
import me.shurik.bettersuggestions.client.data.ClientDataGetter;
import me.shurik.bettersuggestions.client.data.ClientScoreboardValue;
import net.minecraft.entity.Entity;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;

import java.util.Set;

@Mixin(Entity.class)
public class ClientEntityDataMixin implements ClientEntityDataAccessor {
    // Entity highlighting
    private boolean suggestion$highlight = false;
    public boolean isHighlighted() { return suggestion$highlight; }
    public void setHighlighted(boolean highlight) { suggestion$highlight = highlight; }
    // Client command tags
    @Unique private Set<String> suggestions$clientCommandTags = null;
    @Unique private long suggestions$lastTagsUpdateTime = -1L;

    @Nullable
    public Set<String> getClientCommandTags() {
        if (System.currentTimeMillis() - suggestions$lastTagsUpdateTime > Client.POLLING_INTERVAL) {
            ClientDataGetter.requestEntityTags((Entity) (Object) this);
            // Don't request tags [insert FPS here] times per second
            suggestions$lastTagsUpdateTime = System.currentTimeMillis();
        }

        return suggestions$clientCommandTags;
    }

    public void setClientCommandTags(Set<String> clientCommandTags) {
        suggestions$clientCommandTags = clientCommandTags;
        suggestions$lastTagsUpdateTime = System.currentTimeMillis();
    }

    public long getLastTagsUpdateTime() { return suggestions$lastTagsUpdateTime; }

    // Client scoreboard values
    @Unique private Set<ClientScoreboardValue> suggestions$clientScoreboardValues = null;
    @Unique private long suggestions$lastScoresUpdateTime = -1L;

    @Nullable
    public Set<ClientScoreboardValue> getClientScoreboardValues() {
        if (System.currentTimeMillis() - suggestions$lastScoresUpdateTime > Client.POLLING_INTERVAL) {
            ClientDataGetter.requestEntityScores((Entity) (Object) this);
            // Don't request tags [insert FPS here] times per second
            suggestions$lastScoresUpdateTime = System.currentTimeMillis();
        }

        return suggestions$clientScoreboardValues;
    }

    public void setClientScoreboardValues(Set<ClientScoreboardValue> clientScoreboardValues) {
        suggestions$clientScoreboardValues = clientScoreboardValues;
        suggestions$lastScoresUpdateTime = System.currentTimeMillis();
    }
    
    public long getLastScoresUpdateTime() { return suggestions$lastScoresUpdateTime; }
}