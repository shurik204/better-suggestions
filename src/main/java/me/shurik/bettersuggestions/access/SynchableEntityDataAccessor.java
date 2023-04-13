package me.shurik.bettersuggestions.access;

public interface SynchableEntityDataAccessor {
    boolean isDirty();
    void setDirty();
    void setClean();
}