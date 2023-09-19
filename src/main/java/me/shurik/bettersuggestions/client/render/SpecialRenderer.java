package me.shurik.bettersuggestions.client.render;

import com.mojang.blaze3d.systems.RenderSystem;
import net.fabricmc.fabric.api.client.rendering.v1.WorldRenderContext;
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
		BufferBuilder bufferBuilder = tessellator.getBuffer();

		bufferBuilder.begin(VertexFormat.DrawMode.TRIANGLE_STRIP, VertexFormats.POSITION_COLOR);

        return new FullContext(context, tessellator, bufferBuilder, context.matrixStack());
    }

    private static void finishRendering(FullContext context) {
        context.matrices.pop();

        RenderSystem.setShader(GameRenderer::getPositionColorProgram);
        RenderSystem.enableBlend();
        RenderSystem.disableCull();
        RenderSystem.depthFunc(GL11.GL_ALWAYS);
    
        context.tessellator.draw();
    
        RenderSystem.depthFunc(GL11.GL_LEQUAL);
        RenderSystem.enableCull();
        RenderSystem.disableBlend();
    }

    public static void renderEntityHighlight(Entity entity, Vector4f color, WorldRenderContext worldContext) {
        if (entity instanceof InteractionEntity interaction) {
            interactionHighlight(interaction, new Vector4f(0f, 0.8f, 0f, 0.3f), worldContext);
        } else if (entity instanceof DisplayEntity displayEntity) {
            displayEntityHighlight(displayEntity, new Vector4f(0.6f, 0f, 0.6f, 0.3f), worldContext);
        }
    }

    public static void renderBlockHighlight(BlockPos pos, Vector4f color, WorldRenderContext worldContext) {
        FullContext context = setupContext(worldContext, pos);
        // Box box = new Box(-0.5d, 0, -0.5d, 0.5d, 1, 0.5d);

        WorldRenderer.renderFilledBox(context.matrices, context.bufferBuilder, -0.5d, 0, -0.5d, 0.5d, 1, 0.5d, color.x, color.y, color.z, color.w);
        finishRendering(context);
    }

    public static void renderPositionHighlight(Vec3d pos, Vector4f color, WorldRenderContext worldContext) {
        FullContext context = setupContext(worldContext, pos);

        context.matrices.translate(0F, -0.05F, 0F);
        WorldRenderer.renderFilledBox(context.matrices, context.bufferBuilder, -0.05d, 0d, -0.05d, 0.05d, 0.1d, 0.05d, color.x, color.y, color.z, color.w);
        finishRendering(context);
    }

    public static void interactionHighlight(InteractionEntity interaction, Vector4f color, WorldRenderContext worldContext) {
        FullContext context = setupContext(worldContext, interaction);

        double halfX = interaction.getBoundingBox().getXLength() / 2;
        double halfZ = interaction.getBoundingBox().getZLength() / 2;
        // Box box = new Box(-halfX, 0, -halfZ, halfX, interaction.getBoundingBox().getYLength(), halfZ);

        WorldRenderer.renderFilledBox(context.matrices, context.bufferBuilder, -halfX, 0, -halfZ, halfX, interaction.getBoundingBox().getYLength(), halfZ, color.x, color.y, color.z, color.w);
        finishRendering(context);
    }

    public static void displayEntityHighlight(DisplayEntity interaction, Vector4f color, WorldRenderContext worldContext) {
        FullContext context = setupContext(worldContext, interaction);

        context.matrices.translate(0F, -0.2F, 0F);
        WorldRenderer.renderFilledBox(context.matrices, context.bufferBuilder, -0.2d, 0d, -0.2d, 0.2d, 0.4d, 0.2d, color.x, color.y, color.z, color.w);

        finishRendering(context);
    }
}