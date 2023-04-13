package me.shurik.bettersuggestions.mixin.client;

import java.util.Map;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

import net.minecraft.client.resource.language.TranslationStorage;

@Mixin(TranslationStorage.class)
public interface TranslationStorageAccessorMixin {
    @Accessor
    Map<String, String> getTranslations();
}
