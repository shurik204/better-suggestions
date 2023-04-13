package me.shurik.bettersuggestions.utils;

import net.minecraft.registry.Registry;

public class RegistryUtils {
    public static <T> String getName(Registry<T> registry, T entry) {
        return registry.getId(entry).toString();
    }
}
