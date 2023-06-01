package me.shurik.bettersuggestions.mixin.client;

import static me.shurik.bettersuggestions.BetterSuggestionsMod.CONFIG;
import static me.shurik.bettersuggestions.BetterSuggestionsModClient.CLIENT;

import java.util.ArrayList;
import java.util.List;

import org.jetbrains.annotations.Nullable;
import org.lwjgl.glfw.GLFW;
import org.spongepowered.asm.mixin.Debug;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyVariable;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import com.mojang.brigadier.LiteralMessage;
import com.mojang.brigadier.Message;
import com.mojang.brigadier.context.StringRange;
import com.mojang.brigadier.suggestion.Suggestion;

import me.shurik.bettersuggestions.access.CustomSuggestionAccessor;
import me.shurik.bettersuggestions.access.HighlightableEntityAccessor;
import me.shurik.bettersuggestions.utils.ClientUtils;
import me.shurik.bettersuggestions.utils.RegistryUtils;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.gui.screen.ChatInputSuggestor;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.screen.ChatInputSuggestor.SuggestionWindow;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.client.util.math.Rect2i;
import net.minecraft.entity.Entity;
import net.minecraft.registry.Registries;
import net.minecraft.text.Text;
import net.minecraft.util.math.Vec2f;

/**
 * Render tooltip while holding shift
 * Render custom text and tooltip
 * Highlight entities from suggestions
 * Sort suggestions
 */
@Mixin(value = SuggestionWindow.class, priority = 999)
public class SuggestionWindowMixin {
    @Shadow
    private int inWindowIndex;

    @Shadow
    private Vec2f mouse;

    @Shadow
    @Final
    private Rect2i area;
    
    @Shadow
    @Final
    private List<Suggestion> suggestions;
    
    @Shadow
    private int selection;

    @Shadow
    private boolean completed;

    @Inject(at = @At("TAIL"), method = "<init>")
    void init(ChatInputSuggestor suggestor, int x, int y, int width, List<Suggestion> suggestions, boolean narrateFirstSuggestion, CallbackInfo info) {
        // TODO: add color customization for chat and cmd block input
        // suggestor.owner instanceof ChatScreen and suggestor.owner instanceof AbstractCommandBlockScreen
        // if (suggestorAccessor.getOwner() instanceof ChatScreen) {
        //     this.color = 0x00FFFF;
        // } else if (suggestorAccessor.getOwner() instanceof AbstractCommandBlockScreen) {
        //     this.color = 0x00FF00;
        // } else {
        //     this.color = 0xFFFFFF;
        // }
        
        ArrayList<Suggestion> prioritizedSuggestions = new ArrayList<Suggestion>();
        ArrayList<Suggestion> otherSuggestions = new ArrayList<Suggestion>();

        int inputLength = ((ChatInputSuggestorAccessorMixin) suggestor).getTextField().getText().length();
        
        Entity crosshairTarget = ClientUtils.getCrosshairTargetEntity();
        String crosshairTargetUuid = crosshairTarget != null ? crosshairTarget.getUuidAsString() : null;
        
        // Sort all entity UUIDs to be displayed first
        for (int i = 0; i < suggestions.size(); i++) {
            Suggestion suggestion = suggestions.get(i);
            CustomSuggestionAccessor customSuggestion = (CustomSuggestionAccessor) suggestion;
            
            // Prioritize config entries
            // Only if there is some input text
            if (inputLength != suggestion.getRange().getStart() && CONFIG.prioritizedSuggestions.contains(suggestion.getText())) {
                prioritizedSuggestions.add(suggestion);
            }
            // then entity UUIDs
            // (with the exception of the crosshair target, it will always be put first)
            else if (customSuggestion.isEntitySuggestion()) {
                
                // If the crosshair target exists and is the same as the suggestion, put it as first
                if (crosshairTargetUuid != null && crosshairTargetUuid.equals(customSuggestion.getOriginalText())) {
                    prioritizedSuggestions.add(0, suggestion);

                    // Suggest entity selector if enabled
                    if (CONFIG.suggestEntitySelector) {
                        String selector = String.format("@e[type=%s,limit=1,sort=nearest]", RegistryUtils.getName(Registries.ENTITY_TYPE, crosshairTarget.getType()));
                        StringRange stringRange = new StringRange(suggestion.getRange().getStart(), suggestion.getRange().getStart() + selector.length());
                        prioritizedSuggestions.add(1, new Suggestion(stringRange, selector));
                    }
                } else {
                    prioritizedSuggestions.add(suggestion);
                }
            } else {
                otherSuggestions.add(suggestion);
            }
        }
        
        this.suggestions.clear();
        this.suggestions.addAll(prioritizedSuggestions);
        this.suggestions.addAll(otherSuggestions);
        select(0);
    }

    
    @Nullable
    @Unique
    private CustomSuggestionAccessor customCurrentSuggestion;
    
    private boolean renderShiftTooltip;
    // HEAD
    @Inject(method = "render", at = @At("HEAD"))
    void renderPrepare(MatrixStack matrices, int mouseX, int mouseY, CallbackInfo info) {
        renderShiftTooltip = true;
        customCurrentSuggestion = null;
    }

    // Suggestion suggestion = this.suggestions.get(renderIndex + this.inWindowIndex);
    @ModifyVariable(at = @At("STORE"), method = "render", ordinal = 0)
    public Suggestion captureSuggestion(Suggestion suggestion) {
        customCurrentSuggestion = (CustomSuggestionAccessor) suggestion;
        return suggestion;
    }

    // ChatInputSuggestor.this.textRenderer.drawWithShadow(matrices, suggestion.getText()
    @Redirect(method = "render", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/font/TextRenderer;drawWithShadow(Lnet/minecraft/client/util/math/MatrixStack;Ljava/lang/String;FFI)I", ordinal = 0))
    int drawFormattedTextWithShadow(TextRenderer textRenderer, MatrixStack matrices, String __, float x, float y, int color) {
        // Draw as multiline tooltip instead
        return textRenderer.drawWithShadow(matrices, customCurrentSuggestion.getFormattedText(), x, y, color);
    }

    //                                                                           \/
    // if (renderTooltip && (message = this.suggestions.get(this.selection).getTooltip()) != null) ...
    @Redirect(method = "render", at = @At(value = "INVOKE", target = "Lcom/mojang/brigadier/suggestion/Suggestion;getTooltip()Lcom/mojang/brigadier/Message;", ordinal = 0, remap = false))
    Message ifBlockTooltipManipulation(Suggestion suggestion) {
        // If there's custom tooltip, return placeholder to make sure the if block succeeds
        if (((CustomSuggestionAccessor)suggestion).getMultilineTooltip().size() != 0) {
            return new LiteralMessage("placeholder");
        } else {
            // Otherwise, return the original tooltip
            return suggestion.getTooltip();
        }
    }


    @Redirect(method = "render", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/gui/screen/Screen;renderTooltip(Lnet/minecraft/client/util/math/MatrixStack;Lnet/minecraft/text/Text;II)V"))
    void renderMouseTooltip(Screen screen, MatrixStack matrices, Text text, int x, int y) {
        // Render custom tooltip
        CustomSuggestionAccessor customSuggestion = (CustomSuggestionAccessor)this.suggestions.get(this.selection);
        List<Text> tooltip = customSuggestion.getMultilineTooltip();
        if (tooltip != null) {
            CLIENT.currentScreen.renderTooltip(matrices, tooltip, x, y);
        }
        renderShiftTooltip = false;
    }

    @Inject(method = "render", at = @At("TAIL"))
    void renderFinish(MatrixStack matrices, int mouseX, int mouseY, CallbackInfo info) {
        // Render shift tooltip
        if (renderShiftTooltip && Screen.hasShiftDown()) {
            CustomSuggestionAccessor customSuggestion = (CustomSuggestionAccessor)this.suggestions.get(this.selection);
            List<Text> tooltip = customSuggestion.getMultilineTooltip();
            if (tooltip != null) {
                //                                                                                                         get suggestion index in for loop
                CLIENT.currentScreen.renderTooltip(matrices, tooltip, this.area.getX() - 5, this.area.getY() + 2 + 12 * ((this.selection - this.inWindowIndex) - tooltip.size() + 1));
            }
        }

        // Highlight entity from selected suggestion
        CustomSuggestionAccessor customSuggestion = (CustomSuggestionAccessor)this.suggestions.get(this.selection);
        if (customSuggestion.isEntitySuggestion()) {
            Entity entity = customSuggestion.getEntity();
            if (entity != null) {
                ((HighlightableEntityAccessor)entity).setHighlighted(true);
            }
        }
    }

    //public boolean keyPressed(int keyCode, int scanCode, int modifiers)
    @Inject(method = "keyPressed", at=@At("HEAD"), cancellable = true)
    void keyPressed(int keyCode, int scanCode, int modifiers, CallbackInfoReturnable<Boolean> info) {
        if (keyCode == GLFW.GLFW_KEY_UP && modifiers == 2) {
            // Don't forget the minus sign | Wrap around                                      Don't overscroll
            this.scroll(-(this.selection == 0 ? 1 : (this.selection - CONFIG.maxSuggestionsShown < 0 ? this.selection : CONFIG.maxSuggestionsShown)));
            this.completed = false;
            info.setReturnValue(true);
        }
        if (keyCode == GLFW.GLFW_KEY_DOWN && modifiers == 2) {
            //                               Wrap around                                      Don't overscroll
            this.scroll(this.selection == this.suggestions.size() - 1 ? 1 : (this.selection + CONFIG.maxSuggestionsShown >= this.suggestions.size() ? this.suggestions.size() - this.selection - 1 : CONFIG.maxSuggestionsShown));
            this.completed = false;
            info.setReturnValue(true);
        }
    }

    @Shadow
    public void select(int index) {}

    @Shadow
    public void scroll(int offset) {};
}