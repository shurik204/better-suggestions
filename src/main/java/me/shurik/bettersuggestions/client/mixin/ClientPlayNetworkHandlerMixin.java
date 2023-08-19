package me.shurik.bettersuggestions.client.mixin;

import java.util.stream.Collectors;

import com.mojang.brigadier.suggestion.Suggestion;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import me.shurik.bettersuggestions.client.BetterSuggestionsModClient;
import net.minecraft.client.network.ClientPlayNetworkHandler;
import net.minecraft.network.packet.s2c.play.CommandSuggestionsS2CPacket;

@Mixin(ClientPlayNetworkHandler.class)
public class ClientPlayNetworkHandlerMixin {
    @Inject(method="onCommandSuggestions", at=@At(value = "INVOKE",target = "Lnet/minecraft/client/network/ClientCommandSource;onCommandSuggestions(ILcom/mojang/brigadier/suggestion/Suggestions;)V", shift = At.Shift.BEFORE), cancellable = true)
    void captureSuggestions(CommandSuggestionsS2CPacket packet, CallbackInfo info) {
        // Abusing completion packet to store entity id
        // Hopefully no one else uses this or spawns more than a billion entities lol
        if (packet.getCompletionId() < -1_000_000_000 && packet.getCompletionId() > -2_000_000_000) {
            // Convert back
            int entityId = -packet.getCompletionId() - 1_000_000_000;
            // Store tags
            BetterSuggestionsModClient.ENTITY_TAGS.put(entityId, packet.getSuggestions().getList().stream().map(Suggestion::getText).collect(Collectors.toSet()));
            // Stop processing
            info.cancel();
        }
    }
}