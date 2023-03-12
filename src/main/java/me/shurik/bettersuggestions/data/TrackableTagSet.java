package me.shurik.bettersuggestions.data;

import java.util.HashSet;

import me.shurik.bettersuggestions.access.SynchableEntityDataAccessor;
import net.minecraft.entity.Entity;

public class TrackableTagSet extends HashSet<String> {
    private final SynchableEntityDataAccessor entityAccessor;

    public TrackableTagSet(Entity entity) {
        super();
        this.entityAccessor = (SynchableEntityDataAccessor) entity;
    }

    @Override
    public boolean add(String t) {
        boolean result = super.add(t);
        if (result) {
            entityAccessor.setDirty();
        }
        return result;
    }

    @Override
    public boolean remove(Object o) {
        boolean result = super.remove(o);
        if (result) {
            entityAccessor.setDirty();
        }
        return result;
    }

    @Override
    public void clear() {
        super.clear();
        entityAccessor.setDirty();
    }
}