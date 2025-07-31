package me.shurik.bettersuggestions.client.render;

import com.mojang.blaze3d.pipeline.RenderPipeline;
import com.mojang.blaze3d.platform.DepthTestFunction;
import com.mojang.blaze3d.vertex.VertexFormat;
import net.fabricmc.fabric.api.client.rendering.v1.WorldRenderContext;
import net.minecraft.client.gl.RenderPipelines;
import net.minecraft.client.render.*;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.Entity;
import net.minecraft.entity.decoration.DisplayEntity;
import net.minecraft.entity.decoration.InteractionEntity;
import net.minecraft.util.Util;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.Vec3d;
import org.joml.Vector4f;

import java.util.OptionalDouble;
import java.util.function.Function;

import static net.minecraft.client.render.RenderPhase.NO_LAYERING;

public class SpecialRenderer {
    public static final RenderPipeline RENDER_PIPELINE = RenderPipelines.register(
            RenderPipeline.builder(RenderPipelines.POSITION_COLOR_SNIPPET)
                    .withLocation("pipeline/debug_filled_box")
                    .withCull(false)
                    .withDepthTestFunction(DepthTestFunction.NO_DEPTH_TEST)
                    .withVertexFormat(VertexFormats.POSITION_COLOR, VertexFormat.DrawMode.TRIANGLE_STRIP)
                    .build()
    );
    private static final RenderLayer.MultiPhase RENDER_LAYER = RenderLayer.of(
            "better_suggestions_highlight",
            1536,
            false,
            true,
            RENDER_PIPELINE,
            RenderLayer.MultiPhaseParameters.builder()
                    .layering(NO_LAYERING)
                    .build(false)
    );

    private static VertexConsumer setupRendering(WorldRenderContext context, BlockPos pos) { return setupRendering(context, Vec3d.ofBottomCenter(pos)); }
    private static VertexConsumer setupRendering(WorldRenderContext context, Entity entity) { return setupRendering(context, entity.getPos()); }
    private static VertexConsumer setupRendering(WorldRenderContext context, Vec3d pos) {
        Camera camera = context.camera();

        double dx = pos.x - camera.getPos().x;
        double dy = pos.y - camera.getPos().y;
        double dz = pos.z - camera.getPos().z;

        context.matrixStack().push();
        context.matrixStack().translate(dx, dy, dz);

        return context.consumers().getBuffer(RENDER_LAYER);
    }

    private static void finishRendering(WorldRenderContext context) {
        context.matrixStack().pop();
    }

    public static void renderEntityHighlight(Entity entity, Vector4f color, WorldRenderContext worldContext) {
        if (entity instanceof InteractionEntity interaction) {
            interactionHighlight(interaction, new Vector4f(0f, 0.8f, 0f, 0.3f), worldContext);
        } else if (entity instanceof DisplayEntity displayEntity) {
            displayEntityHighlight(displayEntity, new Vector4f(0.6f, 0f, 0.6f, 0.3f), worldContext);
        }
    }

    public static void renderBlockHighlight(BlockPos pos, Vector4f color, WorldRenderContext worldContext) {
        setupRendering(worldContext, pos);

        VertexRendering.drawFilledBox(worldContext.matrixStack(), worldContext.consumers().getBuffer(RENDER_LAYER), -0.05d, 0d, -0.05d, 0.05d, 0.1d, 0.05d, color.x, color.y, color.z, color.w);

        finishRendering(worldContext);
    }

    public static void renderPositionHighlight(Vec3d pos, Vector4f color, WorldRenderContext worldContext) {
        VertexConsumer consumer = setupRendering(worldContext, pos);

        worldContext.matrixStack().translate(0F, -0.05F, 0F);
        VertexRendering.drawFilledBox(worldContext.matrixStack(), consumer, -0.05d, 0d, -0.05d, 0.05d, 0.1d, 0.05d, color.x, color.y, color.z, color.w);

        finishRendering(worldContext);
    }

    public static void interactionHighlight(InteractionEntity interaction, Vector4f color, WorldRenderContext worldContext) {
        VertexConsumer consumer = setupRendering(worldContext, interaction);
        Box box = interaction.getBoundingBox();

        //                   getLengthX
        double halfX = (box.maxX - box.minX) / 2;
        //                   getLengthZ
        double halfZ = (box.maxZ - box.minZ) / 2;
        VertexRendering.drawFilledBox(worldContext.matrixStack(), consumer, -halfX, 0d, -halfZ, halfX, box.maxY - box.minY, halfZ, color.x, color.y, color.z, color.w);

        finishRendering(worldContext);
    }

    public static void displayEntityHighlight(DisplayEntity interaction, Vector4f color, WorldRenderContext worldContext) {
        VertexConsumer consumer = setupRendering(worldContext, interaction);

        worldContext.matrixStack().translate(0F, -0.2F, 0F);
        VertexRendering.drawFilledBox(worldContext.matrixStack(), consumer, -0.2d, 0d, -0.2d, 0.2d, 0.4d, 0.2d, color.x, color.y, color.z, color.w);

        finishRendering(worldContext);
    }

    public static final RenderPipeline TRACER_PIPELINE = RenderPipelines.register(RenderPipeline.builder(RenderPipelines.RENDERTYPE_LINES_SNIPPET).withDepthTestFunction(DepthTestFunction.NO_DEPTH_TEST).withCull(false).withLocation("pipeline/lines").build());
    private static final Function<Double, RenderLayer.MultiPhase> TRACER_LINE = Util.memoize(
            double_ -> RenderLayer.of(
                    "better_suggestions_highlight",
                    1536,
                    TRACER_PIPELINE,
                    RenderLayer.MultiPhaseParameters.builder().lineWidth(new RenderPhase.LineWidth(OptionalDouble.of(double_))).build(false)
            )
    );
    public static void renderTracer(Entity entity, Vector4f color, WorldRenderContext worldContext) {
        // Get camera position (player's view)
        Camera camera = worldContext.camera();
        Vec3d cameraPos = camera.getPos();

        // Get entity position (typically using the center or eye level)
        Vec3d entityPos = entity.getPos().add(0, entity.getHeight() / 2, 0);

        // Setup rendering without translation since we'll specify absolute coordinates
        worldContext.matrixStack().push();
        VertexConsumer consumer = worldContext.consumers().getBuffer(TRACER_LINE.apply(8d));

        // Draw a line from camera to entity (in world space)
        drawLine(
                worldContext.matrixStack(),
                consumer,
                0, 0, 0,  // Start at camera (local origin)
                entityPos.x - cameraPos.x,
                entityPos.y - cameraPos.y,
                entityPos.z - cameraPos.z,  // End at entity (relative to camera)
                color.x, color.y, color.z, color.w
        );

        worldContext.matrixStack().pop();
    }

    public static void drawLine(MatrixStack matrices, VertexConsumer vertices,
                                double startX, double startY, double startZ,
                                double endX, double endY, double endZ,
                                float r, float gr, float b, float a) {
        MatrixStack.Entry matrixEntry = matrices.peek();

        // Test
//        VertexRendering.drawBox(matrices, vertices, startX, startY, startZ, endX, endY, endZ, r, g, b, a);

        float sX = (float)startX;
        float sY = (float)startY;
        float sZ = (float)startZ;
        float eX = (float)endX;
        float eY = (float)endY;
        float eZ = (float)endZ;

        // Draw a line from start to end
//        vertices.vertex(matrixEntry, eX, eY, eZ).color(r, gr, b, a).normal(matrixEntry, 0.0F, 1.0F, 0.0F);
//        vertices.vertex(matrixEntry, sX, sY, sZ).color(r, gr, b, a).normal(matrixEntry, 0.0F, 1.0F, 0.0F);
//        vertices.vertex(matrixEntry, sX, sY, sZ).color(r, gr, b, a).normal(matrixEntry, 1.0F, 0.0F, 0.0F);
//        vertices.vertex(matrixEntry, eX, sY, sZ).color(r, gr, b, a).normal(matrixEntry, 1.0F, 0.0F, 0.0F);
//        vertices.vertex(matrixEntry, sX, sY, sZ).color(r, gr, b, a).normal(matrixEntry, 0.0F, 1.0F, 0.0F);
//        vertices.vertex(matrixEntry, sX, eY, sZ).color(r, gr, b, a).normal(matrixEntry, 0.0F, 1.0F, 0.0F);
//        vertices.vertex(matrixEntry, sX, sY, sZ).color(r, gr, b, a).normal(matrixEntry, 0.0F, 0.0F, 1.0F);
//        vertices.vertex(matrixEntry, sX, sY, eZ).color(r, gr, b, a).normal(matrixEntry, 0.0F, 0.0F, 1.0F);
//        vertices.vertex(matrixEntry, eX, sY, sZ).color(r, gr, b, a).normal(matrixEntry, 0.0F, 1.0F, 0.0F);
//        vertices.vertex(matrixEntry, eX, eY, sZ).color(r, gr, b, a).normal(matrixEntry, 0.0F, 1.0F, 0.0F);
//        vertices.vertex(matrixEntry, eX, eY, sZ).color(r, gr, b, a).normal(matrixEntry, -1.0F, 0.0F, 0.0F);
//        vertices.vertex(matrixEntry, sX, eY, sZ).color(r, gr, b, a).normal(matrixEntry, -1.0F, 0.0F, 0.0F);
//        vertices.vertex(matrixEntry, sX, eY, sZ).color(r, gr, b, a).normal(matrixEntry, 0.0F, 0.0F, 1.0F);
//        vertices.vertex(matrixEntry, sX, eY, eZ).color(r, gr, b, a).normal(matrixEntry, 0.0F, 0.0F, 1.0F);
//        vertices.vertex(matrixEntry, sX, eY, eZ).color(r, gr, b, a).normal(matrixEntry, 0.0F, -1.0F, 0.0F);
//        vertices.vertex(matrixEntry, sX, sY, eZ).color(r, gr, b, a).normal(matrixEntry, 0.0F, -1.0F, 0.0F);
//        vertices.vertex(matrixEntry, sX, sY, eZ).color(r, gr, b, a).normal(matrixEntry, 1.0F, 0.0F, 0.0F);
//        vertices.vertex(matrixEntry, eX, sY, eZ).color(r, gr, b, a).normal(matrixEntry, 1.0F, 0.0F, 0.0F);
//        vertices.vertex(matrixEntry, eX, sY, eZ).color(r, gr, b, a).normal(matrixEntry, 0.0F, 0.0F, -1.0F);
//        vertices.vertex(matrixEntry, eX, sY, sZ).color(r, gr, b, a).normal(matrixEntry, 0.0F, 0.0F, -1.0F);
//        vertices.vertex(matrixEntry, sX, eY, eZ).color(r, gr, b, a).normal(matrixEntry, 1.0F, 0.0F, 0.0F);
//        vertices.vertex(matrixEntry, eX, eY, eZ).color(r, gr, b, a).normal(matrixEntry, 1.0F, 0.0F, 0.0F);
//        vertices.vertex(matrixEntry, eX, sY, eZ).color(r, gr, b, a).normal(matrixEntry, 0.0F, 1.0F, 0.0F);
//        vertices.vertex(matrixEntry, eX, eY, eZ).color(r, gr, b, a).normal(matrixEntry, 0.0F, 1.0F, 0.0F);
//        vertices.vertex(matrixEntry, eX, eY, sZ).color(r, gr, b, a).normal(matrixEntry, 0.0F, 0.0F, 1.0F);
//        vertices.vertex(matrixEntry, eX, eY, eZ).color(r, gr, b, a).normal(matrixEntry, 0.0F, 0.0F, 1.0F);
    }
}