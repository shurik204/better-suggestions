package me.shurik.bettersuggestions.client.mixin;

import com.mojang.brigadier.LiteralMessage;
import com.mojang.brigadier.Message;
import com.mojang.brigadier.context.StringRange;
import com.mojang.brigadier.suggestion.Suggestion;
import me.shurik.bettersuggestions.client.access.ClientEntityDataAccessor;
import me.shurik.bettersuggestions.client.access.CustomSuggestionAccessor;
import me.shurik.bettersuggestions.client.utils.ClientUtils;
import me.shurik.bettersuggestions.utils.RegistryUtils;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.ChatInputSuggestor;
import net.minecraft.client.gui.screen.ChatInputSuggestor.SuggestionWindow;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.util.math.Rect2i;
import net.minecraft.entity.Entity;
import net.minecraft.registry.Registries;
import net.minecraft.text.Text;
import org.jetbrains.annotations.Nullable;
import org.lwjgl.glfw.GLFW;
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

import java.util.ArrayList;
import java.util.List;

import static me.shurik.bettersuggestions.ModConstants.CONFIG;

/**
 * Render tooltip while holding shift
 * Render custom text and tooltip
 * Highlight entities from suggestions
 * Sort suggestions
 */
@Mixin(value = SuggestionWindow.class, priority = 1001)
//                                                1001 - fix incompatibility with Figura mod
public class SuggestionWindowMixin {
    @Shadow @Final ChatInputSuggestor field_21615;
    @Unique
    private static final LiteralMessage PLACEHOLDER_MESSAGE = new LiteralMessage("PLACEHOLDER");

    @Shadow private int inWindowIndex;

    @Shadow @Final private Rect2i area;

    @Shadow @Final private List<Suggestion> suggestions;

    @Shadow private int selection;

    @Shadow private boolean completed;

    @Unique private TextRenderer suggestions$textRenderer;
    @Unique private boolean suggestions$renderShiftTooltip;

    @Unique private boolean isMouseCompletion;

    @Inject(at = @At("TAIL"), method = "<init>")
    void init(ChatInputSuggestor suggestor, int x, int y, int width, List<Suggestion> suggestions, boolean narrateFirstSuggestion, CallbackInfo info) {
        ChatInputSuggestorAccessorMixin suggestorAccessor = (ChatInputSuggestorAccessorMixin) suggestor;
        this.suggestions$textRenderer = suggestorAccessor.getTextRenderer();

        // TODO: add color customization for chat and cmd block input
        // suggestor.owner instanceof ChatScreen and suggestor.owner instanceof AbstractCommandBlockScreen
        // if (suggestorAccessor.getOwner() instanceof ChatScreen) {
        //     this.color = 0x00FFFF;
        // } else if (suggestorAccessor.getOwner() instanceof AbstractCommandBlockScreen) {
        //     this.color = 0x00FF00;
        // } else {
        //     this.color = 0xFFFFFF;
        // }
        
        ArrayList<Suggestion> prioritizedSuggestions = new ArrayList<>();
        ArrayList<Suggestion> otherSuggestions = new ArrayList<>();

        int inputLength = suggestorAccessor.getTextField().getText().length();

        Entity crosshairTarget = ClientUtils.getCrosshairTargetEntity();
        String crosshairTargetUuid = crosshairTarget != null ? crosshairTarget.getUuidAsString() : null;

        // Sort all entity UUIDs to be displayed first
        for (Suggestion suggestion : suggestions) {
            CustomSuggestionAccessor customSuggestion = (CustomSuggestionAccessor) suggestion;

            // Prioritize config entries
            // Only if there is some input text
            if (inputLength != suggestion.getRange().getStart() && CONFIG.prioritizedSuggestions.contains(suggestion.getText())) {
                prioritizedSuggestions.add(suggestion);
            }
            // then entity UUIDs
            // (except the crosshair target, it will always be put first)
            else if (customSuggestion.isEntitySuggestion()) {

                // If the crosshair target exists and is the same as the suggestion, put it as first
                if (crosshairTargetUuid != null && crosshairTargetUuid.equals(customSuggestion.getOriginalText())) {
                    prioritizedSuggestions.add(0, suggestion);

                    // Suggest entity selector if enabled
                    if (CONFIG.entitySuggestions.suggestEntitySelector) {
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

    // HEAD
    @Inject(method = "render", at = @At("HEAD"))
    void renderPrepare(DrawContext context, int mouseX, int mouseY, CallbackInfo info) {
        suggestions$renderShiftTooltip = true;
        customCurrentSuggestion = null;
    }

    // Suggestion suggestion = this.suggestions.get(renderIndex + this.inWindowIndex);
    @ModifyVariable(at = @At(value = "STORE"), method = "render", ordinal = 0)
    public Suggestion captureSuggestion(Suggestion suggestion) {
        customCurrentSuggestion = (CustomSuggestionAccessor) suggestion;
        return suggestion;
    }

    // context.drawTextWithShadow(ChatInputSuggestor.this.textRenderer, suggestion.getText() ...
    @Redirect(method = "render", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/gui/DrawContext;drawTextWithShadow(Lnet/minecraft/client/font/TextRenderer;Ljava/lang/String;III)I", ordinal = 0))
    int drawFormattedTextWithShadow(DrawContext context, TextRenderer textRenderer, String __, int x, int y, int color) {
        // Draw as multiline tooltip instead
        return context.drawTextWithShadow(textRenderer, customCurrentSuggestion.getFormattedText(), x, y, color);
    }

    //                                                                           \/
    // if (renderTooltip && (message = this.suggestions.get(this.selection).getTooltip()) != null) ...
    @Redirect(method = "render", at = @At(value = "INVOKE", target = "Lcom/mojang/brigadier/suggestion/Suggestion;getTooltip()Lcom/mojang/brigadier/Message;", ordinal = 0, remap = false))
    Message ifBlockTooltipManipulation(Suggestion suggestion) {
        // If there's custom tooltip, return placeholder to make sure the if block succeeds
        if (!((CustomSuggestionAccessor) suggestion).getMultilineTooltip().isEmpty()) {
            return PLACEHOLDER_MESSAGE; // Avoid creating a new LiteralMessage every time
        } else {
            // Otherwise, return the original tooltip
            return suggestion.getTooltip();
        }
    }

    // context.drawTooltip(ChatInputSuggestor.this.textRenderer, Texts.toText(message), mouseX, mouseY);
    @Redirect(method = "render", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/gui/DrawContext;drawTooltip(Lnet/minecraft/client/font/TextRenderer;Lnet/minecraft/text/Text;II)V"))
    void renderMouseTooltip(DrawContext context, TextRenderer textRenderer, Text text, int x, int y) {
        // Render custom tooltip
        CustomSuggestionAccessor customSuggestion = (CustomSuggestionAccessor)this.suggestions.get(this.selection);
        List<Text> tooltip = customSuggestion.getMultilineTooltip();
        if (tooltip != null) {
            context.drawTooltip(textRenderer, tooltip, x, y);
        }
        suggestions$renderShiftTooltip = false;
    }

    @Inject(method = "render", at = @At("TAIL"))
    void renderFinish(DrawContext context, int mouseX, int mouseY, CallbackInfo info) {
        CustomSuggestionAccessor customSuggestion = (CustomSuggestionAccessor)this.suggestions.get(this.selection);

//        if (customSuggestion.isBlockPosSuggestion()) {
//            SpecialRendererQueue.addBlock(customSuggestion.getBlockPos());
//        } else if (customSuggestion.isPositionSuggestion()) {
//            SpecialRendererQueue.addPosition(customSuggestion.getPosition());
//        }

        // Render shift tooltip
        if (suggestions$renderShiftTooltip && Screen.hasShiftDown()) {
            List<Text> tooltip = customSuggestion.getMultilineTooltip();
            if (tooltip != null) {
                //                                                                                                             get suggestion index in for loop
                context.drawTooltip(suggestions$textRenderer, tooltip, this.area.getX() - 5, this.area.getY() + (12 * (this.selection - this.inWindowIndex)) - 10 * (tooltip.size() - 1) - 1);
            }
        }

        // Highlight entity from selected suggestion
        if (customSuggestion.isEntitySuggestion()) {
            Entity entity = customSuggestion.getEntity();
            if (entity != null) {
                ((ClientEntityDataAccessor)entity).setHighlighted(true);
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

    @Inject(method = "mouseClicked", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/gui/screen/ChatInputSuggestor$SuggestionWindow;select(I)V", ordinal = 0, shift = At.Shift.AFTER))
    private void markMouseClickCompletion(CallbackInfoReturnable<Boolean> cir) {
        this.isMouseCompletion = true;
    }

    @SuppressWarnings("unchecked")
    @Redirect(method = "complete", at = @At(value = "INVOKE", target = "Ljava/util/List;get(I)Ljava/lang/Object;", ordinal = 0))
    private <E> E modifySuggestionIfNeeded(List<E> list, int index) {
        if (this.isMouseCompletion && CONFIG.addWhitespaceOnMouseCompletion && list.get(index) instanceof Suggestion suggestion) {
            return (E) new Suggestion(
                    suggestion.getRange(),
                suggestion.getText() + " "
            );
        }
        return list.get(index);
    }

    @Inject(method = "complete", at = @At(value = "INVOKE", target = "Lcom/mojang/brigadier/suggestion/Suggestion;apply(Ljava/lang/String;)Ljava/lang/String;", ordinal = 0, shift = At.Shift.AFTER, remap = false))
    private void removeCompletingSuggestionFlag(CallbackInfo ci) {
        this.field_21615.completingSuggestions = !this.isMouseCompletion;
        this.isMouseCompletion = false;
    }

    @Shadow
    public void select(int index) {}

    @Shadow
    public void scroll(int offset) {}
}