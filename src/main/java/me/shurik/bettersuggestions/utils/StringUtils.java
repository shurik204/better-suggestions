package me.shurik.bettersuggestions.utils;

import com.google.common.base.Strings;
import net.minecraft.text.MutableText;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import net.minecraft.util.Uuids;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;

import java.util.Arrays;
import java.util.Collection;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Stream;

public class StringUtils {
    private static final Pattern WHITESPACE_PATTERN = Pattern.compile("(\\s+)");
    
    public static boolean isUUID(String string) {
        return string.matches("[0-9a-f]{8}-[0-9a-f]{4}-[0-9a-f]{4}-[0-9a-f]{4}-[0-9a-f]{12}");
    }

    public static boolean isBlockPos(String string) {
        return string.matches("^-?\\d+ -?\\d+ -?\\d+$");
    }

    public static BlockPos parseBlockPos(String string) {
        String[] split = string.split(" ");
        return new BlockPos(Integer.parseInt(split[0]), Integer.parseInt(split[1]), Integer.parseInt(split[2]));
    }

    public static boolean isPosition(String string) {
        return string.matches("^-?\\d+(?:\\.\\d+)? -?\\d+(?:\\.\\d+)? -?\\d+(?:\\.\\d+)?$");
    }

    public static Vec3d parsePosition(String string) {
        String[] split = string.split(" ");
        return new Vec3d(Double.parseDouble(split[0]), Double.parseDouble(split[1]), Double.parseDouble(split[2]));
    }
    
    // ChatInputSuggestor::getStartOfCurrentWord
    public static int getStartOfCurrentWord(String input) {
        if (Strings.isNullOrEmpty(input)) {
            return 0;
        }
        int i = 0;
        Matcher matcher = WHITESPACE_PATTERN.matcher(input);
        while (matcher.find()) {
            i = matcher.end();
        }
        return i;
    }


    public static Text formatString(String string, Formatting formatting) { return Text.literal(string).formatted(formatting); }
    public static Text formatTranslation(String translation, Formatting formatting) { return Text.translatable(translation).formatted(formatting); }
    public static Text formatInt(int i, Formatting formatting) { return Text.literal(Integer.toString(i)).formatted(formatting); }
    public static Text formatFloat(float d, Formatting formatting) { return Text.literal(String.format("%.2f", d)).formatted(formatting); }
    public static Text formatDouble(double d, Formatting formatting) { return Text.literal(String.format("%.5f", d)).formatted(formatting); }

    public static Text formatPos(Vec3d pos) {
        return Text.translatable("%s %s %s", formatDouble(pos.x, Formatting.RED), formatDouble(pos.y, Formatting.GREEN), formatDouble(pos.z, Formatting.BLUE));
    }
    public static Text formatUuidAsIntArray(UUID uuid) {
        int[] ints = Uuids.toIntArray(uuid);
        return Text.translatable("[%s, %s, %s, %s]",formatInt(ints[0], Formatting.GOLD), formatInt(ints[1], Formatting.GOLD), formatInt(ints[2], Formatting.GOLD), formatInt(ints[3], Formatting.GOLD));
    }
    public static Text formatStrings(Collection<String> strings, Formatting formatting) {
        return formatStrings(strings.stream(), strings.size(), formatting);
    }
    public static Text formatStrings(String[] strings, Formatting formatting) {
        return formatStrings(Arrays.stream(strings), strings.length, formatting);
    }

    private static Text formatStrings(Stream<String> strings, final int count, Formatting formatting) {
        MutableText text = Text.literal("[");
        AtomicInteger i = new AtomicInteger();
        strings.forEach(
            string -> {
                text.append(Text.literal(string).formatted(formatting));
                if (i.getAndIncrement() < count - 1) {
                    text.append(Text.literal(", "));
                }
            }
        );
        text.append(Text.literal("]"));
        return text;
    }

    public static Text joinTexts(Collection<Text> texts) {
        MutableText text = Text.literal("[");
        AtomicInteger i = new AtomicInteger();
        texts.forEach(
            t -> {
                text.append(t);
                if (i.getAndIncrement() < texts.size() - 1) {
                    text.append(Text.literal(", "));
                }
            }
        );
        text.append(Text.literal("]"));
        return text;
    }
}