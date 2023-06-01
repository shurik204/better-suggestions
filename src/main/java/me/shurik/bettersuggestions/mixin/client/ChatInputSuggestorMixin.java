package me.shurik.bettersuggestions.mixin.client;

import static me.shurik.bettersuggestions.BetterSuggestionsMod.CONFIG;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import com.mojang.brigadier.suggestion.Suggestion;

import me.shurik.bettersuggestions.access.CustomSuggestionAccessor;
import net.minecraft.client.gui.screen.ChatInputSuggestor;

/**
 *  Make maxSuggestionSize configurable
 *  Make suggestion window use formatted text for width calculation
 */
@Mixin(ChatInputSuggestor.class)
public class ChatInputSuggestorMixin {
    @Shadow
    int maxSuggestionSize;

    @Redirect(
            method = "show", 
            at = @At(
                value = "INVOKE", 
                target = "Lcom/mojang/brigadier/suggestion/Suggestion;getText()Ljava/lang/String;",
                remap = false
                )
            )
    String getAsFormattedText(Suggestion suggestion) {
        return ((CustomSuggestionAccessor)suggestion).getFormattedText().getString();
    }

    @Inject(method = "show",at = @At("HEAD"))
    void setMaxSuggestionSize(boolean narrateFirstSuggestion, CallbackInfo info) {
        maxSuggestionSize = CONFIG.maxSuggestionsShown;
    }
}