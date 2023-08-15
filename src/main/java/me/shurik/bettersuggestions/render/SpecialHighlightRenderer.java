package me.shurik.bettersuggestions.render;

import org.lwjgl.opengl.GL11;

import com.mojang.blaze3d.systems.RenderSystem;
import net.fabricmc.fabric.api.client.rendering.v1.WorldRenderContext;
import net.minecraft.client.render.BufferBuilder;
import net.minecraft.client.render.Camera;
import net.minecraft.client.render.GameRenderer;
import net.minecraft.client.render.Tessellator;
import net.minecraft.client.render.VertexFormat;
import net.minecraft.client.render.VertexFormats;
import net.minecraft.client.render.WorldRenderer;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.Entity;
import net.minecraft.entity.decoration.DisplayEntity;
import net.minecraft.entity.decoration.InteractionEntity;
import net.minecraft.util.math.Box;

public class SpecialHighlightRenderer {
    private record FullContext(WorldRenderContext worldContext, Tessellator tessellator, BufferBuilder bufferBuilder, MatrixStack matrices) {}

    private static FullContext setupContext(WorldRenderContext context, Entity entity) {
        Camera camera = context.camera();

        double dx = entity.getX() - camera.getPos().x;
        double dy = entity.getY() - camera.getPos().y;
        double dz = entity.getZ() - camera.getPos().z;

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

    public static void interaction(InteractionEntity interaction, WorldRenderContext worldContext) {
        FullContext context = setupContext(worldContext, interaction);

        double halfX = interaction.getBoundingBox().getXLength() / 2;
        double halfZ = interaction.getBoundingBox().getZLength() / 2;
        Box box = new Box(-halfX, 0, -halfZ, halfX, interaction.getBoundingBox().getYLength(), halfZ);

        WorldRenderer.renderFilledBox(context.matrices, context.bufferBuilder, box.minX, box.minY, box.minZ, box.maxX, box.maxY, box.maxZ, 0f, 0.8f, 0f, 0.3f);

        finishRendering(context);
    }

    public static void displayEntity(DisplayEntity interaction, WorldRenderContext worldContext) {
        FullContext context = setupContext(worldContext, interaction);

        Box box = new Box(-0.2d, 0d, -0.2d, 0.2d, 0.4d, 0.2d);

        context.matrices.translate(0F, -0.2F, 0F);
        WorldRenderer.renderFilledBox(context.matrices, context.bufferBuilder, box.minX, box.minY, box.minZ, box.maxX, box.maxY, box.maxZ, 0.6f, 0f, 0.6f, 0.3f);

        finishRendering(context);
    }
}