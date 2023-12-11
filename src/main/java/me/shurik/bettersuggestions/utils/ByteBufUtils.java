package me.shurik.bettersuggestions.utils;

import io.netty.buffer.Unpooled;
import io.netty.util.CharsetUtil;
import me.shurik.bettersuggestions.client.data.ClientScoreboardValue;
import me.shurik.bettersuggestions.utils.Scoreboards.ScoreboardScoreContainer;

import net.minecraft.network.PacketByteBuf;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Set;
import java.util.function.BiConsumer;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

public class ByteBufUtils {
    private static final PacketByteBuf emptyBuffer = new PacketByteBuf(Unpooled.EMPTY_BUFFER);
    public static PacketByteBuf empty() {
        return emptyBuffer;
    }

    public static PacketByteBuf withString(String string) {
        return new PacketByteBuf(Unpooled.copiedBuffer(string, CharsetUtil.UTF_8));
    }

    public static PacketByteBuf withInt(int i) {
        return new PacketByteBuf(Unpooled.copyInt(i));
    }

    public static void writeScoreboardValue(PacketByteBuf buf, ScoreboardScoreContainer container) {
        // Why tf getObjective() is nullable, if it's clearly not?
        buf.writeString(container.objective().getName());
        buf.writeInt(container.score().getScore());
    }

    public static ClientScoreboardValue readScoreboardValue(PacketByteBuf buf) {
        return new ClientScoreboardValue(buf.readString(32767), buf.readInt());
    }

    public static <T> PacketByteBuf writeCollection(PacketByteBuf buffer, Collection<T> collection, BiConsumer<PacketByteBuf, T> writer) {
        buffer.writeInt(collection.size());
        for (T t : collection) {
            writer.accept(buffer, t);
        }
        return buffer;
    }

    public static <T> Collection<T> readCollection(PacketByteBuf buffer, Function<PacketByteBuf, T> reader) {
        int size = buffer.readInt();
        Collection<T> collection = new ArrayList<>(size);
        for (int i = 0; i < size; i++) {
            collection.add(reader.apply(buffer));
        }
        return collection;
    }

    public static <T> Set<T> readSet(PacketByteBuf buffer, Function<PacketByteBuf, T> reader) {
        int size = buffer.readInt();
        return IntStream.range(0, size).mapToObj(i -> reader.apply(buffer)).collect(Collectors.toSet());
    }
}