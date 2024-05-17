package me.shurik.bettersuggestions.utils;

import net.minecraft.registry.DynamicRegistryManager;
import net.minecraft.registry.Registry;
import org.jetbrains.annotations.Nullable;

import java.lang.reflect.Method;

public class RegistryUtils {
    public static <T> String getName(Registry<T> registry, T entry) {
        return registry.getId(entry).toString();
    }
}
