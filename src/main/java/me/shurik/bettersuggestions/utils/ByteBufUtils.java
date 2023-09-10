package me.shurik.bettersuggestions.utils;

import io.netty.buffer.Unpooled;
import io.netty.util.CharsetUtil;
import net.minecraft.network.PacketByteBuf;

import java.util.Set;
import java.util.function.BiConsumer;

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

    public static <T> PacketByteBuf packSet(PacketByteBuf buf, Set<T> set, BiConsumer<PacketByteBuf, T> packer) {
        buf.writeInt(set.size());
        for (T t : set) {
            packer.accept(buf, t);
        }
        return buf;
    }
}
