package me.shurik.bettersuggestions.mixin;

import org.spongepowered.asm.mixin.Mixin;

import me.shurik.bettersuggestions.access.HighlightableEntityAccessor;
import net.minecraft.entity.Entity;

@Mixin(Entity.class)
public class EntityOutlineMixin implements HighlightableEntityAccessor {
    private boolean suggestion$highlight = false;

    public boolean isHighlighted() {
        return suggestion$highlight;
    }

    public void setHighlighted(boolean highlight) {
        suggestion$highlight = highlight;
    }
}