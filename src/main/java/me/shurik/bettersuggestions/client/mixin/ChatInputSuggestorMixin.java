package me.shurik.bettersuggestions.client.mixin;

import com.google.common.collect.Lists;
import com.mojang.brigadier.ParseResults;
import com.mojang.brigadier.context.ParsedArgument;
import com.mojang.brigadier.suggestion.Suggestion;
import me.shurik.bettersuggestions.client.access.CustomSuggestionAccessor;
import me.shurik.bettersuggestions.client.render.SpecialRendererQueue;
import me.shurik.bettersuggestions.utils.ColorUtils;
import me.shurik.bettersuggestions.utils.StringUtils;
import net.minecraft.client.gui.screen.ChatInputSuggestor;
import net.minecraft.client.gui.widget.TextFieldWidget;
import net.minecraft.command.CommandSource;
import net.minecraft.command.argument.DefaultPosArgument;
import net.minecraft.util.Formatting;
import net.minecraft.util.math.BlockPos;
import org.joml.Vector4f;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.List;

import static me.shurik.bettersuggestions.ModConstants.CONFIG;

/**
 *  Make maxSuggestionSize configurable
 *  Make suggestion window use formatted text for width calculation
 */
@Mixin(ChatInputSuggestor.class)
public class ChatInputSuggestorMixin {
    private static final List<SpecialRendererQueue.BlockEntry> blockRenderQueue = Lists.newArrayList();
    static {
        // Add this list to the block highlight renderer queue
        SpecialRendererQueue.BLOCKS.addList("chatInputSuggestor", blockRenderQueue);
    }
    private static final Vector4f[] COLORS = new Vector4f[] { ColorUtils.getColor(Formatting.AQUA.getColorValue(), 0.3f), ColorUtils.getColor(Formatting.YELLOW.getColorValue(), 0.3f), ColorUtils.getColor(Formatting.GREEN.getColorValue(), 0.3f), ColorUtils.getColor(Formatting.LIGHT_PURPLE.getColorValue(), 0.3f), ColorUtils.getColor(Formatting.GOLD.getColorValue(), 0.3f) };
    private static Vector4f getColorForIndex(int index) {
        return COLORS[index % COLORS.length];
    }

    @Shadow int maxSuggestionSize;

    @Shadow private ParseResults<CommandSource> parse;

    @Shadow @Final
    TextFieldWidget textField;

    @Redirect(method = "show", at = @At(value = "INVOKE", target = "Lcom/mojang/brigadier/suggestion/Suggestion;getText()Ljava/lang/String;", remap = false))
    String getAsFormattedText(Suggestion suggestion) { return ((CustomSuggestionAccessor)suggestion).getFormattedText().getString(); }

    @Inject(method = "show",at = @At("HEAD"))
    void setMaxSuggestionSize(boolean narrateFirstSuggestion, CallbackInfo info) { maxSuggestionSize = CONFIG.maxSuggestionsShown; }

    @Inject(method = "refresh", at = @At("TAIL"))
    void grabCoordinates(CallbackInfo ci) {
        // Clear own block highlights
        blockRenderQueue.clear();
        if (parse == null) {
            return;
        }

        for (ParsedArgument<CommandSource, ?> parsedArgument : parse.getContext().getLastChild().getArguments().values()) {
            if (parsedArgument.getResult() instanceof DefaultPosArgument) {
                BlockPos pos = StringUtils.parseBlockPos(parsedArgument.getRange().get(parse.getReader()));
                blockRenderQueue.add(new SpecialRendererQueue.BlockEntry(pos, getColorForIndex(blockRenderQueue.size())));
            }
        }
    }
}