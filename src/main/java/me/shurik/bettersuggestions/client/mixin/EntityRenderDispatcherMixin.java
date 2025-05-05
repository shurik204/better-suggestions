package me.shurik.bettersuggestions.client.mixin;

import me.shurik.bettersuggestions.client.access.ClientEntityDataAccessor;
import me.shurik.bettersuggestions.client.render.SpecialRendererQueue;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.render.Camera;
import net.minecraft.client.render.Frustum;
import net.minecraft.client.render.OverlayTexture;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.entity.EntityRenderDispatcher;
import net.minecraft.client.render.entity.EntityRenderer;
import net.minecraft.client.render.entity.state.EntityRenderState;
import net.minecraft.client.render.item.HeldItemRenderer;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.AreaEffectCloudEntity;
import net.minecraft.entity.Entity;
import net.minecraft.entity.MarkerEntity;
import net.minecraft.entity.decoration.DisplayEntity;
import net.minecraft.entity.decoration.InteractionEntity;
import net.minecraft.item.ItemDisplayContext;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import org.joml.Quaternionf;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(EntityRenderDispatcher.class)
public abstract class EntityRenderDispatcherMixin {
    @Shadow public abstract Quaternionf getRotation();

    @Shadow @Final private HeldItemRenderer heldItemRenderer;

    @Inject(method = "shouldRender", at = @At("HEAD"), cancellable = true)
    private <E extends Entity> void shouldRender(E entity, Frustum frustum, double x, double y, double z, CallbackInfoReturnable<Boolean> info) {
        //                                                                                                             Only ignore display entity view range when highlighted (fixes #16)
        if (entity instanceof AreaEffectCloudEntity || (entity instanceof DisplayEntity && ((ClientEntityDataAccessor)entity).isHighlighted())) {
            info.setReturnValue(true);
        }
    }

    @Inject(method = "render(Lnet/minecraft/entity/Entity;DDDFLnet/minecraft/client/util/math/MatrixStack;Lnet/minecraft/client/render/VertexConsumerProvider;ILnet/minecraft/client/render/entity/EntityRenderer;)V", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/render/entity/EntityRenderDispatcher;render(Lnet/minecraft/client/render/entity/state/EntityRenderState;DDDLnet/minecraft/client/util/math/MatrixStack;Lnet/minecraft/client/render/VertexConsumerProvider;ILnet/minecraft/client/render/entity/EntityRenderer;)V"))
    private <E extends Entity, S extends EntityRenderState> void highlightSpecial(E entity, double x, double y, double z, float tickDelta, MatrixStack matrices, VertexConsumerProvider vertexConsumers, int light, EntityRenderer<? super E, S> renderer, CallbackInfo ci) {
        if (((ClientEntityDataAccessor)entity).isHighlighted()) {
            switch (entity) {
                case MarkerEntity unused ->
//                    SpecialRendererQueue.addEntity(entity);
                        suggestions$renderItem(Items.STRUCTURE_VOID.getDefaultStack(), light, matrices, vertexConsumers, entity);
                case AreaEffectCloudEntity unused ->
//                        SpecialRendererQueue.addEntity(entity);
                        suggestions$renderItem(Items.LINGERING_POTION.getDefaultStack(), light, matrices, vertexConsumers, entity);
                case DisplayEntity unused ->
                        SpecialRendererQueue.addEntity(entity);
                case InteractionEntity unused ->
                        SpecialRendererQueue.addEntity(entity);
                // In case the proper renderer is broken:
                // case InteractionEntity interaction -> suggestions$renderItem(Items.PISTON.getDefaultStack(), light, matrices, vertexConsumers, entity);
                // case DisplayEntity display -> suggestions$renderItem(Items.ITEM_FRAME.getDefaultStack(), light, matrices, vertexConsumers, entity);
                default -> {}
            }
        }
    }

    private void suggestions$renderItem(ItemStack item, int light, MatrixStack matrices, VertexConsumerProvider vertexConsumers, Entity entity) {
        matrices.push();

        // In 1.21.5 matrix is not on the entity's position anymore, so we need to move it manually
        Camera camera = MinecraftClient.getInstance().gameRenderer.getCamera();
        matrices.translate(entity.getX() - camera.getPos().x, entity.getY() - camera.getPos().y, entity.getZ() - camera.getPos().z);
        matrices.multiply(this.getRotation()); // Rotate item to face camera
        matrices.scale(0.8f, 0.8f, 0.8f);

        MinecraftClient.getInstance().getItemRenderer().renderItem(item, ItemDisplayContext.FIXED, light, OverlayTexture.DEFAULT_UV, matrices, vertexConsumers, entity.getWorld(), entity.getId());
        matrices.pop();
    }
}