package me.shurik.bettersuggestions.mixin;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityDimensions;
import net.minecraft.entity.EntityType;

@Mixin(EntityType.Builder.class)
public class EntityTypeBuilderMixin<T extends Entity> {
    @Shadow
    private EntityDimensions dimensions;

    @Shadow
    private int maxTrackingRange;
    
    @Inject(at = @At("TAIL"), method = "maxTrackingRange", cancellable = true)
    public void maxTrackingRange(int maxTrackRange, CallbackInfoReturnable<EntityType.Builder<?>> info) {
        if (maxTrackRange == 0 && dimensions.width == 0.0F && dimensions.height == 0.0F) {
            maxTrackingRange = 6;
            dimensions = EntityDimensions.fixed(0.0F, 0.0F);
        }
    }
}