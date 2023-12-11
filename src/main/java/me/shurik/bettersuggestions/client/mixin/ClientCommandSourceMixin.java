package me.shurik.bettersuggestions.client.mixin;

import net.minecraft.client.network.ClientCommandSource;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.PlayerEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.Collection;

import static me.shurik.bettersuggestions.ModConstants.CONFIG;
import static me.shurik.bettersuggestions.client.Client.INSTANCE;

/**
 * Suggest nearby entities in selectors.
 */
@Mixin(ClientCommandSource.class)
public class ClientCommandSourceMixin {
    @Inject(at = @At("HEAD"), method = "getEntitySuggestions", cancellable = true)
    private void suggestNearbyEntities(CallbackInfoReturnable<Collection<String>> info) {
        if (INSTANCE.world != null && INSTANCE.player != null) {
            info.setReturnValue(INSTANCE.world.getOtherEntities(null, INSTANCE.player.getBoundingBox().expand(CONFIG.entitySuggestions.entitySuggestionRadius), (entity) -> !(entity instanceof PlayerEntity)).stream().map(Entity::getNameForScoreboard).toList());
        }
    }
}