package me.shurik.bettersuggestions.client.access;

import org.jetbrains.annotations.Nullable;

import java.util.Set;

public interface ClientEntityDataAccessor {
    // Entity highlighting
    boolean isHighlighted();
    void setHighlighted(boolean highlight);
    // Client command tags
    @Nullable
    Set<String> getClientCommandTags();
    void setClientCommandTags(Set<String> clientCommandTags);
    long getLastTagsUpdateTime();
}