package me.shurik.bettersuggestions.client.mixin;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import me.shurik.bettersuggestions.client.access.HighlightableEntityAccessor;
import net.minecraft.client.MinecraftClient;
import net.minecraft.entity.Entity;

/**
 * Show outline for highlighted entities.
 */
@Mixin(MinecraftClient.class)
public class MinecraftClientMixin {
    @Inject(at = @At("HEAD"), method = "hasOutline", cancellable = true)
    private void hasOutline(Entity entity, CallbackInfoReturnable<Boolean> info) {
        if (((HighlightableEntityAccessor) entity).isHighlighted()) {
            info.setReturnValue(true);
        }
    }
}