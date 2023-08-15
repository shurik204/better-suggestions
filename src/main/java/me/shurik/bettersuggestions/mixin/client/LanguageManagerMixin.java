package me.shurik.bettersuggestions.mixin.client;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import me.shurik.bettersuggestions.utils.text.TextCompletions;
import net.minecraft.client.resource.language.LanguageManager;
import net.minecraft.resource.ResourceManager;

/**
 * Clear translation cache when the language manager reloads.
 */
@Mixin(LanguageManager.class)
public class LanguageManagerMixin {
    // public void reload(ResourceManager manager)
    @Inject(at = @At("HEAD"), method = "reload")
    void reload(ResourceManager manager, CallbackInfo info) {
        TextCompletions.TRANSLATION_CACHE.clear();
    }
}