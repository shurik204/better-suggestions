package me.shurik.bettersuggestions.mixin;

import me.shurik.bettersuggestions.ModConstants;
import net.minecraft.command.CommandSource;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.server.command.ServerCommandSource;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.Collection;

@Mixin(CommandSource.class)
public interface CommandSourceMixin {
    // public Collection<String> getEntitySuggestions()
    @Inject(at = @At("HEAD"), method = "getEntitySuggestions", cancellable = true)
    default void getEntitySuggestions(CallbackInfoReturnable<Collection<String>> info) {
        if (((Object) this) instanceof ServerCommandSource serverCommandSource) {
            info.setReturnValue(serverCommandSource.getWorld().getOtherEntities(null, serverCommandSource.getPlayer().getBoundingBox().expand(ModConstants.CONFIG.entitySuggestions.entitySuggestionRadius), (entity) -> !(entity instanceof PlayerEntity)).stream().map(Entity::getEntityName).toList());
        }
    }
}