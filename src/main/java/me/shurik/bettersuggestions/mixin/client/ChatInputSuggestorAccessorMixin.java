package me.shurik.bettersuggestions.mixin.client;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

import net.minecraft.client.gui.screen.ChatInputSuggestor;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.widget.TextFieldWidget;

/**
 * Accessor for ChatInputSuggestor
 */
@Mixin(ChatInputSuggestor.class)
public interface ChatInputSuggestorAccessorMixin {
    @Accessor
    Screen getOwner();

    @Accessor
    TextFieldWidget getTextField();

    @Accessor
    void setMaxSuggestionSize(int maxSuggestionSize);
}
