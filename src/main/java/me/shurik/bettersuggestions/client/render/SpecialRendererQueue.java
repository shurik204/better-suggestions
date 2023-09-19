package me.shurik.bettersuggestions.client.render;

import com.google.common.collect.Iterables;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import me.shurik.bettersuggestions.utils.ColorUtils;
import net.fabricmc.fabric.api.client.rendering.v1.WorldRenderContext;
import net.minecraft.entity.Entity;
import net.minecraft.util.Formatting;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import org.jetbrains.annotations.NotNull;
import org.joml.Vector4f;

import java.util.Iterator;
import java.util.List;
import java.util.Map;

public class SpecialRendererQueue {
    private interface Entry {}
    public record BlockEntry(BlockPos pos, Vector4f color) implements Entry {}
    public record PositionEntry(Vec3d pos, Vector4f color) implements Entry {}
    public record EntityEntry(Entity entity, Vector4f color) implements Entry {}
    public static final Queue<BlockEntry> BLOCKS = new Queue<>();
    public static final Queue<PositionEntry> POSITIONS = new Queue<>();
    public static final Queue<EntityEntry> ENTITIES = new Queue<>();

    private static final Vector4f[] COLORS = new Vector4f[] { ColorUtils.getColor(Formatting.RED.getColorValue(), 0.3f), ColorUtils.getColor(Formatting.GREEN.getColorValue(), 0.3f), ColorUtils.getColor(Formatting.YELLOW.getColorValue(), 0.3f), ColorUtils.getColor(Formatting.LIGHT_PURPLE.getColorValue(), 0.3f) };
    public static Vector4f getColorForIndex(int index) {
        return COLORS[index % COLORS.length];
    }

    public static void addBlock(BlockPos pos) {
        BLOCKS.add(new BlockEntry(pos, getColorForIndex(BLOCKS.size())));
    }

    public static void addPosition(Vec3d pos) {
        POSITIONS.add(new PositionEntry(pos, getColorForIndex(POSITIONS.size())));
    }

    public static void addEntity(Entity entity) {
        ENTITIES.add(new EntityEntry(entity, getColorForIndex(ENTITIES.size())));
    }

    public static void clearQueue() {
        BLOCKS.clear();
        POSITIONS.clear();
        ENTITIES.clear();
    }

    public static void clearAll() {
        BLOCKS.clearAll();
        POSITIONS.clearAll();
        ENTITIES.clearAll();
    }

    public static void processQueue(WorldRenderContext worldContext) {
        for (BlockEntry entry : BLOCKS) {
            SpecialRenderer.renderBlockHighlight(entry.pos, entry.color, worldContext);
        }
        for (PositionEntry entry : POSITIONS) {
            SpecialRenderer.renderPositionHighlight(entry.pos, entry.color, worldContext);
        }
        for (EntityEntry entry : ENTITIES) {
            SpecialRenderer.renderEntityHighlight(entry.entity, entry.color, worldContext);
        }

        clearQueue();
    }

    public static class Queue<E extends Entry> implements Iterable<E> {
        // Internal queue is cleared each pass
        private final List<E> internalQueue = Lists.newArrayList();
        private final Map<String, List<E>> externalQueueList = Maps.newHashMap();

        public void add(E entry) {
            internalQueue.add(entry);
        }

        public int size() {
            return internalQueue.size();
        }

        public void addList(String key, List<E> list) {
            if (externalQueueList.containsKey(key)) {
                throw new IllegalArgumentException("List '" + key + "' already exists");
            }
            externalQueueList.put(key, list);
        }

        public boolean listExists(String key) {
            return externalQueueList.containsKey(key);
        }

        public boolean clearListIfExists(String key) {
            if (externalQueueList.containsKey(key)) {
                externalQueueList.get(key).clear();
                return true;
            }
            return false;
        }

        public List<E> getList(String key) {
            if (!externalQueueList.containsKey(key)) {
                throw new IllegalArgumentException("List '" + key + "' does not exist");
            }
            return externalQueueList.get(key);
        }

        public void removeList(String key) {
            externalQueueList.remove(key);
        }

        public void clear() {
            internalQueue.clear();
        }

        public void clearAll() {
            internalQueue.clear();
            externalQueueList.values().forEach(List::clear);
        }

        @Override
        @NotNull
        public Iterator<E> iterator() {
            // :pain:
            return Iterables.concat(internalQueue, Iterables.concat(externalQueueList.values())).iterator();
        }
    }
}