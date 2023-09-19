package me.shurik.bettersuggestions.utils;

import org.joml.Vector4f;

public class ColorUtils {

    public static Vector4f getColor(int color, float alpha) {
        return new Vector4f(
                ((color >> 16) & 0xFF) / 255f,
                ((color >> 8) & 0xFF) / 255f,
                (color & 0xFF) / 255f,
                alpha
        );
    }
}
