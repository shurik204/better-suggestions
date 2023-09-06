package me.shurik.bettersuggestions.client.mixin;

import com.google.common.collect.Lists;
import com.mojang.brigadier.Message;
import com.mojang.brigadier.context.StringRange;
import com.mojang.brigadier.suggestion.Suggestion;
import me.shurik.bettersuggestions.client.BetterSuggestionsModClient;
import me.shurik.bettersuggestions.client.access.CustomSuggestionAccessor;
import me.shurik.bettersuggestions.client.utils.ClientUtils;
import me.shurik.bettersuggestions.client.utils.FallbackTagGetter;
import me.shurik.bettersuggestions.utils.StringUtils;
import net.minecraft.entity.Entity;
import net.minecraft.registry.Registries;
import net.minecraft.text.Text;
import net.minecraft.util.Uuids;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.List;
import java.util.Set;
import java.util.UUID;

/**
 * Add information to suggestion tooltip.
 */
@Mixin(Suggestion.class)
public class SuggestionMixin implements CustomSuggestionAccessor {
    @Final
    @Shadow(remap = false)
    private String text;

    private boolean entitySuggestion = false;
    private boolean blockPosSuggestion = false;
    private boolean positionSuggestion = false;

    private Entity suggestions$entity;
    private Vec3d suggestions$position = null;
    private BlockPos suggestions$blockPos = null;

    @Inject(at=@At("RETURN"), method="<init>(Lcom/mojang/brigadier/context/StringRange;Ljava/lang/String;Lcom/mojang/brigadier/Message;)V", remap = false)
    private void init(final StringRange range, final String text, final Message tooltip, CallbackInfo info) {
        if (text == null) return;

        if (StringUtils.isUUID(text)) {
            entitySuggestion = true;
            suggestions$entity =  ClientUtils.getEntityByUUID(text);
        }
//        else if (StringUtils.isBlockPos(text)) {
//            blockPosSuggestion = true;
//            suggestions$blockPos = StringUtils.parseBlockPos(text);
//        }
//        else if (StringUtils.isPosition(text)) {
//            positionSuggestion = true;
//            suggestions$position = StringUtils.parsePosition(text);
//        }
    }

    public boolean isEntitySuggestion() { return entitySuggestion; }
    public boolean isPositionSuggestion() { return positionSuggestion; }
    public boolean isBlockPosSuggestion() { return blockPosSuggestion; }

    public List<Text> getMultilineTooltip() {
        List<Text> tooltip = Lists.newArrayList();
        
        if (entitySuggestion) {
            Entity entity = getEntity();
            if (entity != null) {
                tooltip.add(Text.of(Registries.ENTITY_TYPE.getId(getEntity().getType()).toString()));

                // Try to request tags periodically if the mod is not present on server
                if (!BetterSuggestionsModClient.MOD_PRESENT_ON_SERVER) {
                    FallbackTagGetter.tryRequestEntityTags(entity);
                }

                Set<String> tags = BetterSuggestionsModClient.ENTITY_TAGS.get(entity.getId());

                if (tags != null)
                    tooltip.add(Text.translatable("text.suggestion.tooltip.entity_tags", tags.size(), tags.toString()));
                else {
                    tooltip.add(Text.translatable("text.suggestion.tooltip.loading_tags"));
                }

                if (entity.getVehicle() != null) {
                    tooltip.add(Text.translatable("text.suggestion.tooltip.vehicle", entity.getVehicle().getName().getString()));
                }

                return tooltip;
            }
        }

        // Default tooltip (only shows up for non-entity suggestions or if entity doesn't exist)
        Message tooltipMessage = ((Suggestion) (Object) this).getTooltip();
        if (tooltipMessage != null) {
            tooltip.add(Text.of(tooltipMessage.getString()));
        }

        return tooltip;
    }

    @Nullable
    public Entity getEntity() {
        if (suggestions$entity == null || !ClientUtils.entityExists(suggestions$entity.getId())) {
            suggestions$entity = ClientUtils.getEntityByUUID(text);
        }

        return suggestions$entity;
    }

    @Override
    public Vec3d getPosition() {
        if (positionSuggestion) {
            return suggestions$position;
        }

        return null;
    }

    @Override
    public BlockPos getBlockPos() {
        if (blockPosSuggestion) {
            return suggestions$blockPos;
        }

        return null;
    }

    public Text getFormattedText() {
        if (entitySuggestion) {
            Entity entity = getEntity();
            if (entity != null) {
                return Text.translatable("%s (%s)", Text.literal(text), entity.getName());
            }
        }
        
        return Text.of(text);
    }

    public String getTextWithEntityId() {
        if (entitySuggestion) {
            Entity entity = getEntity();
            if (entity != null) {
                int[] uuid = Uuids.toIntArray(UUID.fromString(text));
                // uuid to string int array
                return "[" + uuid[0] + " " + uuid[1] + " " + uuid[2] + " " + uuid[3] + "] (" + entity.getName().getString() + ")";
            }
        }
        return text;
    }

    public String getOriginalText() {
        return text;
    }
}