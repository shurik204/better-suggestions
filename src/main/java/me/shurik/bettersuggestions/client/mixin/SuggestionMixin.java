package me.shurik.bettersuggestions.client.mixin;

import com.google.common.collect.Lists;
import com.mojang.brigadier.Message;
import com.mojang.brigadier.context.StringRange;
import com.mojang.brigadier.suggestion.Suggestion;
import me.shurik.bettersuggestions.ModConstants;
import me.shurik.bettersuggestions.client.Client;
import me.shurik.bettersuggestions.client.access.ClientEntityDataAccessor;
import me.shurik.bettersuggestions.client.access.CustomSuggestionAccessor;
import me.shurik.bettersuggestions.client.data.ClientScoreboardValue;
import me.shurik.bettersuggestions.client.utils.ClientUtils;
import me.shurik.bettersuggestions.utils.StringUtils;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.registry.Registries;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
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
@Mixin(value = Suggestion.class, remap = false)
public class SuggestionMixin implements CustomSuggestionAccessor {
    @Final
    @Shadow
    private String text;

    private boolean entitySuggestion = false;
    private boolean blockPosSuggestion = false;
    private boolean positionSuggestion = false;

    private Entity suggestions$entity;
    private Vec3d suggestions$position = null;
    private BlockPos suggestions$blockPos = null;

    @Inject(at=@At("RETURN"), method="<init>(Lcom/mojang/brigadier/context/StringRange;Ljava/lang/String;Lcom/mojang/brigadier/Message;)V")
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
                if (ModConstants.CONFIG.entitySuggestions.showEntityId) {
                    tooltip.add(StringUtils.formatString(Registries.ENTITY_TYPE.getId(getEntity().getType()).toString(), Formatting.GREEN));
                }

                if (ModConstants.CONFIG.entitySuggestions.showEntityUuid) {
                    tooltip.add(Text.translatable("text.suggestion.tooltip.uuid.layout",
                            StringUtils.formatTranslation("text.suggestion.tooltip.uuid", Formatting.AQUA),
                            StringUtils.formatUuidAsIntArray(entity.getUuid())
                    ));
                }

                if (ModConstants.CONFIG.entitySuggestions.showEntityPos) {
                    tooltip.add(Text.translatable("text.suggestion.tooltip.pos.layout",
                            StringUtils.formatTranslation("text.suggestion.tooltip.pos", Formatting.AQUA),
                            StringUtils.formatPos(entity.getPos())
                    ));
                }

                if (ModConstants.CONFIG.entitySuggestions.showEntityTags) {
                    // Get entity tags
                    Set<String> tags = ((ClientEntityDataAccessor) entity).getClientCommandTags();
                    // No info
                    if (tags == null) {
                        tooltip.add(Text.translatable("text.suggestion.tooltip.entity_tags.loading").formatted(Formatting.GRAY));
                    }
                    // Check if entity has any tags
                    else if (!tags.isEmpty()) {
                        tooltip.add(Text.translatable("text.suggestion.tooltip.entity_tags.layout",
                                StringUtils.formatTranslation("text.suggestion.tooltip.entity_tags", Formatting.AQUA),
                                StringUtils.formatInt(tags.size(), Formatting.GOLD),
                                StringUtils.formatStrings(tags, Formatting.GREEN)
                        ));
                    }
                }

                if (ModConstants.CONFIG.entitySuggestions.showEntityVehicle && entity.getVehicle() != null) {
                    tooltip.add(Text.translatable("text.suggestion.tooltip.vehicle.layout",
                            StringUtils.formatTranslation("text.suggestion.tooltip.vehicle", Formatting.AQUA),
                            StringUtils.formatString(entity.getVehicle().getName().getString(), Formatting.GREEN)
                    ));
                }

                if (ModConstants.CONFIG.entitySuggestions.showEntityPassengers && !entity.getPassengerList().isEmpty()) {
                    // Display number of passengers and their names
                    tooltip.add(Text.translatable("text.suggestion.tooltip.passengers.layout",
                            StringUtils.formatTranslation("text.suggestion.tooltip.passengers", Formatting.AQUA),
                            StringUtils.formatInt(entity.getPassengerList().size(), Formatting.GOLD),
                            StringUtils.joinTexts(entity.getPassengerList().stream().map(Entity::getName).toList())
                    ));
                }

                if (ModConstants.CONFIG.entitySuggestions.showEntityTeam && entity.getScoreboardTeam() != null) {
                    tooltip.add(Text.translatable("text.suggestion.tooltip.team.layout",
                            StringUtils.formatTranslation("text.suggestion.tooltip.team", Formatting.AQUA),
                            Text.literal(entity.getScoreboardTeam().getName()).styled(style -> style.withColor(entity.getScoreboardTeam().getColor()))
                    ));
                }

                if (ModConstants.CONFIG.entitySuggestions.showEntityHealth && entity instanceof LivingEntity livingEntity) {
                    tooltip.add(Text.translatable("text.suggestion.tooltip.health.layout",
                            StringUtils.formatTranslation("text.suggestion.tooltip.health", Formatting.AQUA),
                            StringUtils.formatFloat(livingEntity.getHealth(), Formatting.RED),
                            StringUtils.formatFloat(livingEntity.getMaxHealth(), Formatting.RED)
                    ));
                }

                if (ModConstants.CONFIG.entitySuggestions.showEntityScores) {
                    // Get entity tags
                    Set<ClientScoreboardValue> scoreboardValues = ((ClientEntityDataAccessor) entity).getClientScoreboardValues();
                    // No info & no server side
                    if (!Client.SERVER_SIDE_PRESENT) {
                        tooltip.add(StringUtils.formatTranslation("text.suggestion.tooltip.entity_scores.no_server_side", Formatting.GRAY));
                    }
                    else if (scoreboardValues == null) {
                        tooltip.add(StringUtils.formatTranslation("text.suggestion.tooltip.entity_scores.loading", Formatting.GRAY));
                    }
                    // Check if entity has any scores
                    else if (!scoreboardValues.isEmpty()) {
                        tooltip.add(Text.translatable("text.suggestion.tooltip.entity_scores.first_layout",
                                StringUtils.formatTranslation("text.suggestion.tooltip.entity_scores", Formatting.AQUA)
                        ));
                        tooltip.addAll(scoreboardValues.stream().map(value -> Text.translatable("text.suggestion.tooltip.entity_scores.layout",
                                StringUtils.formatString(value.objective(), Formatting.GRAY),
                                StringUtils.formatInt(value.score(), Formatting.YELLOW)
                        )).toList());
                    }
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