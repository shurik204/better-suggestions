package me.shurik.bettersuggestions.client.mixin;

import net.minecraft.client.render.Frustum;
import net.minecraft.client.render.entity.EntityRenderer;
import net.minecraft.entity.AreaEffectCloudEntity;
import net.minecraft.entity.Entity;
import net.minecraft.entity.MarkerEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

/**
 * Render markers and area effect clouds as items.
 */
@Mixin(EntityRenderer.class)
public class EntityRendererMixin<T extends Entity> {
//    private ItemRenderer suggestions$itemRenderer;
//    private EntityRenderDispatcher suggestions$dispatcher;
    
    // constructor
//    @Inject(method = "<init>", at = @At("RETURN"))
//    void constructor(EntityRendererFactory.Context ctx, CallbackInfo info) {
//        this.suggestions$itemRenderer = ctx.getItemRenderer();
//        this.suggestions$dispatcher = ctx.getRenderDispatcher();
//    }

    // public boolean shouldRender(T entity, Frustum frustum, double x, double y, double z)
    @Inject(method = "shouldRender", at = @At("HEAD"), cancellable = true)
    void shouldRender(T entity, Frustum frustum, double x, double y, double z, CallbackInfoReturnable<Boolean> info) {
        if (entity instanceof MarkerEntity || entity instanceof AreaEffectCloudEntity) {
            info.setReturnValue(true);
        }
    }

    // public void render(<S extends EntityRenderState> state, MatrixStack matrices, VertexConsumerProvider vertexConsumers, int light)
//    @Inject(method = "render", at = @At("HEAD"))
//    <S extends EntityRenderState> void render(S state, MatrixStack matrices, VertexConsumerProvider vertexConsumers, int light, CallbackInfo ci) {
//
//    }
}