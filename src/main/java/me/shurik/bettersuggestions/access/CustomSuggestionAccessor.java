package me.shurik.bettersuggestions.access;

import java.util.List;

import org.jetbrains.annotations.Nullable;

import net.minecraft.entity.Entity;
import net.minecraft.text.Text;

public interface CustomSuggestionAccessor {
    @Nullable
    Entity getEntity();
    boolean isEntitySuggestion();
    Text getFormattedText();
    String getOriginalText();
    String getTextWithEntityId();
    List<Text> getMultilineTooltip();
}
