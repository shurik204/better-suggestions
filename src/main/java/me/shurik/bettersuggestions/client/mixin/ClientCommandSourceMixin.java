package me.shurik.bettersuggestions.client.mixin;

import static me.shurik.bettersuggestions.BetterSuggestionsMod.CONFIG;
import static me.shurik.bettersuggestions.client.BetterSuggestionsModClient.CLIENT;

import java.util.Collection;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import net.minecraft.client.network.ClientCommandSource;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.PlayerEntity;

/**
 * Suggest nearby entities in selectors.
 */
@Mixin(ClientCommandSource.class)
public class ClientCommandSourceMixin {
    @Inject(at = @At("HEAD"), method = "getEntitySuggestions", cancellable = true)
    private void suggestNearbyEntities(CallbackInfoReturnable<Collection<String>> info) {
        if (CLIENT.world != null) {
            info.setReturnValue(CLIENT.world.getOtherEntities(null, CLIENT.player.getBoundingBox().expand(CONFIG.entitySuggestionRadius), (entity) -> !(entity instanceof PlayerEntity)).stream().map(Entity::getEntityName).toList());
        }
    }
}