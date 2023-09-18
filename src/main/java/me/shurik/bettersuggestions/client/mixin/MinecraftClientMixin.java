package me.shurik.bettersuggestions.client.mixin;

import me.shurik.bettersuggestions.client.access.ClientEntityDataAccessor;
import me.shurik.bettersuggestions.client.render.SpecialRendererQueue;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.entity.Entity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

/**
 * Show outline for highlighted entities.
 * Clean up ChatInputSuggestor render queue when closing screen.
 */
@Mixin(MinecraftClient.class)
public abstract class MinecraftClientMixin {
    @Inject(at = @At("HEAD"), method = "hasOutline", cancellable = true)
    private void hasOutline(Entity entity, CallbackInfoReturnable<Boolean> info) {
        if (((ClientEntityDataAccessor) entity).isHighlighted()) {
            info.setReturnValue(true);
        }
    }

    @Inject(at = @At("HEAD"), method = "setScreen")
    private void clearRenderQueueIfNeeded(Screen screen, CallbackInfo info) {
        if (screen == null) {
            SpecialRendererQueue.BLOCKS.clearListIfExists("chatInputSuggestor");
            SpecialRendererQueue.POSITIONS.clearListIfExists("chatInputSuggestor");
        }
    }
}