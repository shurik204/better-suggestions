package me.shurik.bettersuggestions.mixin.client;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;


import me.shurik.bettersuggestions.BetterSuggestionsModClient;
import net.minecraft.network.ClientConnection;

@Mixin(ClientConnection.class)
public class ClientConnectionMixin {
    // void disconnect(Text disconnectReason)
    @Inject(at = @At("HEAD"), method = "disconnect")
    void disconnect(CallbackInfo info) {
        // When the client disconnects, clear the scoreboard tags cache
        BetterSuggestionsModClient.ENTITY_TAGS.clear();
    }

}
