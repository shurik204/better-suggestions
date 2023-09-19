package me.shurik.bettersuggestions.client.mixin;

import com.mojang.brigadier.arguments.ArgumentType;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.suggestion.Suggestions;
import com.mojang.brigadier.suggestion.SuggestionsBuilder;
import me.shurik.bettersuggestions.client.suggestion.SwizzleArgumentTypeSuggestions;
import me.shurik.bettersuggestions.client.suggestion.TextArgumentSuggestions;
import me.shurik.bettersuggestions.client.suggestion.UuidArgumentSuggestions;
import net.minecraft.command.argument.SwizzleArgumentType;
import net.minecraft.command.argument.TextArgumentType;
import net.minecraft.command.argument.UuidArgumentType;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.concurrent.CompletableFuture;

/**
 * Add suggestions for argument types that don't normally have them.
 */
@Mixin(ArgumentType.class)
public interface ArgumentTypeMixin {
    // default <S> CompletableFuture<Suggestions> listSuggestions(final CommandContext<S> context, final SuggestionsBuilder builder)
    @Inject(at = @At("HEAD"), method = "listSuggestions", cancellable = true, remap = false)
    default <S> void listSuggestions(final CommandContext<S> context, final SuggestionsBuilder builder, CallbackInfoReturnable<CompletableFuture<Suggestions>> info) {
        // this
        ArgumentType<?> argumentType = (ArgumentType<?>) this;

        if (argumentType instanceof TextArgumentType) {
            info.setReturnValue(TextArgumentSuggestions.listSuggestions(context, builder));
        }
        // Suggest random UUID (attribute modifier)
        else if (argumentType instanceof UuidArgumentType) {
            info.setReturnValue(UuidArgumentSuggestions.listSuggestions(context, builder));
        }
        else if (argumentType instanceof SwizzleArgumentType) {
            info.setReturnValue(SwizzleArgumentTypeSuggestions.listSuggestions(context, builder));
        }
    }
}