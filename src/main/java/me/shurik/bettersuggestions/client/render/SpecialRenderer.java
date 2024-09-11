package me.shurik.bettersuggestions.client.render;

import com.mojang.blaze3d.systems.RenderSystem;
import net.fabricmc.fabric.api.client.rendering.v1.WorldRenderContext;
import net.minecraft.client.gl.ShaderProgram;
import net.minecraft.client.gl.ShaderProgramKeys;
import net.minecraft.client.render.*;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.Entity;
import net.minecraft.entity.decoration.DisplayEntity;
import net.minecraft.entity.decoration.InteractionEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import org.joml.Vector4f;
import org.lwjgl.opengl.GL11;

public class SpecialRenderer {
    private record FullContext(WorldRenderContext worldContext, Tessellator tessellator, BufferBuilder bufferBuilder, MatrixStack matrices) {}

    private static FullContext setupContext(WorldRenderContext context, BlockPos pos) { return setupContext(context, Vec3d.ofBottomCenter(pos)); }
    private static FullContext setupContext(WorldRenderContext context, Entity entity) { return setupContext(context, entity.getPos()); }
    private static FullContext setupContext(WorldRenderContext context, Vec3d pos) {
        Camera camera = context.camera();

        double dx = pos.x - camera.getPos().x;
        double dy = pos.y - camera.getPos().y;
        double dz = pos.z - camera.getPos().z;

        context.matrixStack().push();
        context.matrixStack().translate(dx, dy, dz);

        Tessellator tessellator = Tessellator.getInstance();
		BufferBuilder bufferBuilder = tessellator.begin(VertexFormat.DrawMode.TRIANGLE_STRIP, VertexFormats.POSITION_COLOR);

        return new FullContext(context, tessellator, bufferBuilder, context.matrixStack());
    }

    private static void finishRendering(FullContext context) {
        context.matrices.pop();

        try (ShaderProgram shader = RenderSystem.setShader(ShaderProgramKeys.POSITION_COLOR)) {
            RenderSystem.enableBlend();
            RenderSystem.disableCull();
            RenderSystem.depthFunc(GL11.GL_ALWAYS);

            BufferRenderer.draw(context.bufferBuilder.end());

            RenderSystem.depthFunc(GL11.GL_LEQUAL);
            RenderSystem.enableCull();
            RenderSystem.disableBlend();
        }
    }

    public static void renderEntityHighlight(Entity entity, Vector4f color, WorldRenderContext worldContext) {
        if (entity instanceof InteractionEntity interaction) {
            interactionHighlight(interaction, new Vector4f(0f, 0.8f, 0f, 0.3f), worldContext);
        } else if (entity instanceof DisplayEntity displayEntity) {
            displayEntityHighlight(displayEntity, new Vector4f(0.6f, 0f, 0.6f, 0.3f), worldContext);
        }
    }

    public static void renderBlockHighlight(BlockPos pos, Vector4f color, WorldRenderContext worldContext) {
//        FullContext context = setupContext(worldContext, pos);
//
//        VertexRendering.drawFilledBox(context.matrices, context.bufferBuilder, -0.05d, 0d, -0.05d, 0.05d, 0.1d, 0.05d, color.x, color.y, color.z, color.w);
//
//        finishRendering(context);
    }

    public static void renderPositionHighlight(Vec3d pos, Vector4f color, WorldRenderContext worldContext) {
//        FullContext context = setupContext(worldContext, pos);
//
//        context.matrices.translate(0F, -0.05F, 0F);
////        DebugRenderer.drawBox(context.matrices, worldContext.consumers(), -0.05d, 0d, -0.05d, 0.05d, 0.1d, 0.05d, color.x, color.y, color.z, color.w);
//        VertexRendering.drawFilledBox(context.matrices, context.bufferBuilder, -0.05d, 0d, -0.05d, 0.05d, 0.1d, 0.05d, color.x, color.y, color.z, color.w);
//        finishRendering(context);
    }

    public static void interactionHighlight(InteractionEntity interaction, Vector4f color, WorldRenderContext worldContext) {
//        FullContext context = setupContext(worldContext, interaction);
//        Box box = interaction.getBoundingBox();
//
//        //                   getLengthX
//        double halfX = (box.maxX - box.minX) / 2;
//        //                   getLengthZ
//        double halfZ = (box.maxZ - box.minZ) / 2;
//
//        VertexRendering.drawFilledBox(context.matrices, context.bufferBuilder, -0.05d, 0d, -0.05d, 0.05d, 0.1d, 0.05d, color.x, color.y, color.z, color.w);
//        finishRendering(context);
    }

    public static void displayEntityHighlight(DisplayEntity interaction, Vector4f color, WorldRenderContext worldContext) {
//        FullContext context = setupContext(worldContext, interaction);
//
//        context.matrices.translate(0F, -0.2F, 0F);
//        VertexRendering.drawBox(context.matrices, context.bufferBuilder, -0.2d, 0d, -0.2d, 0.2d, 0.4d, 0.2d, color.x, color.y, color.z, color.w);
//
//        finishRendering(context);
    }
}