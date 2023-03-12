package me.shurik.bettersuggestions.access;

import java.util.Set;

public interface SynchableEntityDataAccessor {
    Set<String> getClientTags();
    void setClientTags(Set<String> tags);
    boolean isDirty();
    void setDirty();
    void setClean();
}