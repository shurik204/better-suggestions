package me.shurik.bettersuggestions.client.mixin;

import me.shurik.bettersuggestions.client.access.HighlightableEntityAccessor;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.entity.Entity;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

/**
 * Show outline for highlighted entities.
 * Store the command when the chat screen is closed.
 */
@Mixin(MinecraftClient.class)
public abstract class MinecraftClientMixin {
    @Shadow @Nullable public Screen currentScreen;

    @Inject(at = @At("HEAD"), method = "hasOutline", cancellable = true)
    private void hasOutline(Entity entity, CallbackInfoReturnable<Boolean> info) {
        if (((HighlightableEntityAccessor) entity).isHighlighted()) {
            info.setReturnValue(true);
        }
    }
}