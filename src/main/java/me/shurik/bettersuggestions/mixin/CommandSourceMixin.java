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

        // if (CLIENT.world != null) {
            // Collection<String> suggestions = Lists.newArrayList();
            // String crosshairTargetUuid = null;

            // if (client.crosshairTarget != null && client.crosshairTarget.getType() == HitResult.Type.ENTITY) {
            //     crosshairTargetUuid = ((EntityHitResult) client.crosshairTarget).getEntity().getUuidAsString();
            // }

            // if (crosshairTargetUuid != null) {
            //     suggestions.add(crosshairTargetUuid);
            //     // Needs to be final to be used in lambda
            //     final String crosshairTargetUuidFinal = crosshairTargetUuid;
            //     suggestions.addAll(client.world.getOtherEntities(null, client.player.getBoundingBox().expand(10), (entity) -> !(entity instanceof PlayerEntity) && !entity.getUuidAsString().equals(crosshairTargetUuidFinal)).stream().map(Entity::getEntityName).toList());
            // } else {
            //     suggestions.addAll(client.world.getOtherEntities(null, client.player.getBoundingBox().expand(10), (entity) -> !(entity instanceof PlayerEntity)).stream().map(Entity::getEntityName).toList());
            // }
            
            // info.setReturnValue(suggestions);

            // info.setReturnValue(CLIENT.world.getOtherEntities(null, CLIENT.player.getBoundingBox().expand(10), (entity) -> !(entity instanceof PlayerEntity)).stream().map(Entity::getEntityName).toList());
        // }
    }
}