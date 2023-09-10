package me.shurik.bettersuggestions.client.mixin;

import me.shurik.bettersuggestions.client.Client;
import me.shurik.bettersuggestions.client.render.SpecialRendererQueue;
import net.minecraft.client.gui.screen.ChatScreen;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.widget.TextFieldWidget;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import static me.shurik.bettersuggestions.ModConstants.CONFIG;

@Mixin(ChatScreen.class)
public class ChatScreenMixin {
    @Shadow protected TextFieldWidget chatField;
    @Shadow private String originalChatText;
    private static String suggestions$storedCommand = null;

    @Inject(at = @At("TAIL"), method = "<init>")
    void restoreCommand(CallbackInfo ci) {
        if (suggestions$storedCommand != null) {
            originalChatText = suggestions$storedCommand;
            suggestions$storedCommand = null;
        }
    }

    @Inject(at = @At("HEAD"), method = "removed")
    void storeCommand(CallbackInfo ci) {
        if (Client.escapePressed) {
            if (CONFIG.rememberCommandOnEscape && !Screen.hasShiftDown() && chatField.getText().startsWith("/")) {
                suggestions$storedCommand = chatField.getText();
            }
        }
//        else
        if (SpecialRendererQueue.BLOCKS.listExists("chatInputSuggestor")) {
            SpecialRendererQueue.BLOCKS.getList("chatInputSuggestor").clear();
        }
    }
}