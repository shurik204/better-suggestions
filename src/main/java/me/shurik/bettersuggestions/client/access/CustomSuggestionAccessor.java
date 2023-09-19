package me.shurik.bettersuggestions.client.access;

import net.minecraft.entity.Entity;
import net.minecraft.text.Text;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public interface CustomSuggestionAccessor {
    @Nullable
    Entity getEntity();
    Vec3d getPosition();
    BlockPos getBlockPos();
    boolean isEntitySuggestion();
    boolean isPositionSuggestion();
    boolean isBlockPosSuggestion();
    Text getFormattedText();
    String getOriginalText();
    String getTextWithEntityId();
    List<Text> getMultilineTooltip();
}
