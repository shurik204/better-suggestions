package me.shurik.bettersuggestions.client.mixin;

import me.shurik.bettersuggestions.client.Client;
import me.shurik.bettersuggestions.client.utils.ClientUtils;
import net.minecraft.client.gui.screen.ChatScreen;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.widget.TextFieldWidget;
import org.lwjgl.glfw.GLFW;
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

    @Inject(at = @At("TAIL"), method = "<init>")
    void restoreCommand(CallbackInfo ci) {
        if (Client.storedChatCommand != null) {
            originalChatText = Client.storedChatCommand;
            Client.storedChatCommand = null;
        }
    }

    @Inject(at = @At("HEAD"), method = "removed")
    void storeCommand(CallbackInfo ci) {
        if (ClientUtils.isKeyPressed(GLFW.GLFW_KEY_ESCAPE) && CONFIG.rememberCommandOnEscape && !Screen.hasShiftDown() && chatField.getText().startsWith("/")) {
            Client.storedChatCommand = chatField.getText();
        }
    }
}