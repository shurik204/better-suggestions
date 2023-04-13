package me.shurik.bettersuggestions.mixin;

import java.util.Collection;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import net.minecraft.command.CommandSource;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.server.command.ServerCommandSource;

@Mixin(CommandSource.class)
public interface CommandSourceMixin {
    // public Collection<String> getEntitySuggestions()
    @Inject(at = @At("HEAD"), method = "getEntitySuggestions", cancellable = true)
    default void getEntitySuggestions(CallbackInfoReturnable<Collection<String>> info) {
        // this
        CommandSource commandSource = (CommandSource) (Object) this;

        if (commandSource instanceof ServerCommandSource serverCommandSource) {
            // serverCommandSource.getServer()
            info.setReturnValue(serverCommandSource.getWorld().getOtherEntities(null, serverCommandSource.getPlayer().getBoundingBox().expand(10), (entity) -> !(entity instanceof PlayerEntity)).stream().map(Entity::getEntityName).toList());
        }
    }
}