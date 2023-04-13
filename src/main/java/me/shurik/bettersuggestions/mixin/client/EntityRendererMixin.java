package me.shurik.bettersuggestions.mixin.client;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import me.shurik.bettersuggestions.access.HighlightableEntityAccessor;
import net.minecraft.client.render.Frustum;
import net.minecraft.client.render.OverlayTexture;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.entity.EntityRenderDispatcher;
import net.minecraft.client.render.entity.EntityRenderer;
import net.minecraft.client.render.entity.EntityRendererFactory;
import net.minecraft.client.render.item.ItemRenderer;
import net.minecraft.client.render.model.json.ModelTransformationMode;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.AreaEffectCloudEntity;
import net.minecraft.entity.Entity;
import net.minecraft.entity.MarkerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.util.math.RotationAxis;

/**
 * Render markers and area effect clouds as items.
 */
@Mixin(EntityRenderer.class)
public class EntityRendererMixin<T extends Entity> {
    private ItemRenderer suggestions$itemRenderer;
    private EntityRenderDispatcher suggestions$dispatcher;
    

    // constructor
    @Inject(method = "<init>", at = @At("RETURN"))
    void constructor(EntityRendererFactory.Context ctx, CallbackInfo info) {
        this.suggestions$itemRenderer = ctx.getItemRenderer();
        this.suggestions$dispatcher = ctx.getRenderDispatcher();
    }

    // public boolean shouldRender(T entity, Frustum frustum, double x, double y, double z)
    @Inject(method = "shouldRender", at = @At("HEAD"), cancellable = true)
    void shouldRender(T entity, Frustum frustum, double x, double y, double z, CallbackInfoReturnable<Boolean> info) {
        if (entity instanceof MarkerEntity || entity instanceof AreaEffectCloudEntity) {
            info.setReturnValue(true);
        }
    }

    // public void render(T entity, float yaw, float tickDelta, MatrixStack matrices, VertexConsumerProvider vertexConsumers, int light)
    @Inject(method = "render", at = @At("HEAD"), cancellable = true)
    void render(T entity, float yaw, float tickDelta, MatrixStack matrices, VertexConsumerProvider vertexConsumers, int light, CallbackInfo info) {
        if ((entity instanceof MarkerEntity || entity instanceof AreaEffectCloudEntity) && ((HighlightableEntityAccessor)entity).isHighlighted()) {
            ItemStack item;
            matrices.push();
            matrices.multiply(this.suggestions$dispatcher.getRotation());
            matrices.multiply(RotationAxis.POSITIVE_Y.rotationDegrees(180.0F));
            if (entity instanceof MarkerEntity) {
                matrices.scale(1F, 1F, 1F);
                item = Items.STRUCTURE_VOID.getDefaultStack();
            } else {
                matrices.scale(1F, 1F, 1F);
                item = Items.LINGERING_POTION.getDefaultStack();
            }
			
			this.suggestions$itemRenderer.renderItem(item, ModelTransformationMode.GROUND, light, OverlayTexture.DEFAULT_UV, matrices, vertexConsumers, entity.world, entity.getId());
			matrices.pop();
        }
    }
}