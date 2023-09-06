package me.shurik.bettersuggestions.utils;

import com.google.common.base.Strings;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

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
}
