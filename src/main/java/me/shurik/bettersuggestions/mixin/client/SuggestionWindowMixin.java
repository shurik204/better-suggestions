package me.shurik.bettersuggestions.mixin.client;

import static me.shurik.bettersuggestions.BetterSuggestionsModClient.CLIENT;

import java.util.ArrayList;
import java.util.List;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Overwrite;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import com.mojang.brigadier.suggestion.Suggestion;

import me.shurik.bettersuggestions.access.CustomSuggestionAccessor;
import me.shurik.bettersuggestions.access.HighlightableEntityAccessor;
import me.shurik.bettersuggestions.utils.ClientUtils;
import net.minecraft.client.gui.DrawableHelper;
import net.minecraft.client.gui.screen.ChatInputSuggestor;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.screen.ChatInputSuggestor.SuggestionWindow;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.client.util.math.Rect2i;
import net.minecraft.entity.Entity;
import net.minecraft.text.Text;
import net.minecraft.util.math.Vec2f;

@Mixin(SuggestionWindow.class)
public class SuggestionWindowMixin {
    // TODO: make maxSuggestionSize configurable
    private static final int maxSuggestionsShown = 10;
    private int color;

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

    // private ChatInputSuggestor suggestor;

    @Inject(at = @At("TAIL"), method = "<init>")
    void init(ChatInputSuggestor suggestor, int x, int y, int width, List<Suggestion> suggestions, boolean narrateFirstSuggestion, CallbackInfo info) {
        // this.suggestor = suggestor;
        this.color = suggestor.color;

        ArrayList<Suggestion> uuids = new ArrayList<Suggestion>();
        ArrayList<Suggestion> otherEntries = new ArrayList<Suggestion>();
        
        Entity crosshairTarget = ClientUtils.getCrosshairTargetEntity();
        String crosshairTargetUuid = crosshairTarget != null ? crosshairTarget.getUuidAsString() : null;
        
        // Sort all entity UUIDs to be displayed first
        for (int i = 0; i < suggestions.size(); i++) {
            Suggestion suggestion = suggestions.get(i);
            CustomSuggestionAccessor customSuggestion = (CustomSuggestionAccessor) suggestion;
            if (customSuggestion.isEntitySuggestion()) {
                // If the crosshair target exists and is the same as the suggestion, put it as first
                if (crosshairTargetUuid != null && crosshairTargetUuid.equals(customSuggestion.getOriginalText())) {
                    uuids.add(0, suggestion);
                } else {
                    uuids.add(suggestion);
                }
            } else {
                otherEntries.add(suggestion);
            }
        }

        this.suggestions.clear();
        this.suggestions.addAll(uuids);
        this.suggestions.addAll(otherEntries);
        select(0);
    }

    /**
     * @author shurik204
     * @reason There's a better way to do this by injecting near the `boolean bl5 = false;` line, but I counldn't get it to work.
     */
    @Overwrite
    public void render(MatrixStack matrices, int mouseX, int mouseY) {
        int i = Math.min(this.suggestions.size(), maxSuggestionsShown);
        boolean renderTopScrollIndicator = this.inWindowIndex > 0;
        boolean renderBottomScrollIndicator = this.suggestions.size() > this.inWindowIndex + i;
        boolean renderAnyScrollIndicator = renderTopScrollIndicator || renderBottomScrollIndicator;
        boolean updateMousePos = this.mouse.x != (float)mouseX || this.mouse.y != (float)mouseY;
        if (updateMousePos) {
            this.mouse = new Vec2f((float)mouseX, (float)mouseY);
        }

        if (renderAnyScrollIndicator) {
            DrawableHelper.fill(matrices, this.area.getX(), this.area.getY() - 1, this.area.getX() + this.area.getWidth(), this.area.getY(), color);
            DrawableHelper.fill(matrices, this.area.getX(), this.area.getY() + this.area.getHeight(), this.area.getX() + this.area.getWidth(), this.area.getY() + this.area.getHeight() + 1, color);
            int k;
            if (renderTopScrollIndicator) {
                for(k = 0; k < this.area.getWidth(); ++k) {
                    if (k % 2 == 0) {
                        DrawableHelper.fill((MatrixStack)matrices, this.area.getX() + k, this.area.getY() - 1, this.area.getX() + k + 1, this.area.getY(), -1);
                    }
                }
            }

            if (renderBottomScrollIndicator) {
                for(k = 0; k < this.area.getWidth(); ++k) {
                    if (k % 2 == 0) {
                        DrawableHelper.fill((MatrixStack)matrices, this.area.getX() + k, this.area.getY() + this.area.getHeight(), this.area.getX() + k + 1, this.area.getY() + this.area.getHeight() + 1, -1);
                    }
                }
            }
        }

        boolean renderTooltipNearMouse = false;
        int selectedIndexInForLoop = -1;

        for(int l = 0; l < i; ++l) {
            Suggestion suggestion = (Suggestion)this.suggestions.get(l + this.inWindowIndex);
            DrawableHelper.fill(matrices, this.area.getX(), this.area.getY() + 12 * l, this.area.getX() + this.area.getWidth(), this.area.getY() + 12 * l + 12, color);
            //                                                                                          >= to prevent the suggestion from bouncing around with shift key held
            if (mouseX > this.area.getX() && mouseX < this.area.getX() + this.area.getWidth() && mouseY >= this.area.getY() + 12 * l && mouseY < this.area.getY() + 12 * l + 12) {
                if (updateMousePos) {
                    select(l + this.inWindowIndex);
                }

                renderTooltipNearMouse = true;
            }

            CustomSuggestionAccessor customSuggestion = (CustomSuggestionAccessor)suggestion;
            if (l + this.inWindowIndex == this.selection) {
                selectedIndexInForLoop = l;
                if (customSuggestion.isEntitySuggestion()) {
                    Entity entity = customSuggestion.getEntity();
                    if (entity != null) {
                        ((HighlightableEntityAccessor)entity).setHighlighted(true);
                    }
                }
            }

            CLIENT.textRenderer.drawWithShadow(matrices, customSuggestion.getFormattedText(), (float)(this.area.getX() + 1), (float)(this.area.getY() + 2 + 12 * l), l + this.inWindowIndex == this.selection ? -256 : -5592406);
        }

        if (renderTooltipNearMouse) {
            List<Text> tooltip = ((CustomSuggestionAccessor)this.suggestions.get(this.selection)).getMultilineTooltip();
            if (tooltip != null) {
                // suggestions$client.currentScreen.renderTooltip(matrices, Texts.toText(message).copy().append("\nTesting"), this.area.getX() - 5, this.area.getY() + 2 + 12 * selectedIndexInForLoop);
                CLIENT.currentScreen.renderTooltip(matrices, tooltip, mouseX, mouseY);
            }
        } else {
            if (Screen.hasShiftDown()) {
                List<Text> tooltip = ((CustomSuggestionAccessor)this.suggestions.get(this.selection)).getMultilineTooltip();
                // List<OrderedText> tooltip = ((Suggestion)this.suggestions.get(this.selection)).getTooltip();
                if (tooltip != null) {
                    CLIENT.currentScreen.renderTooltip(matrices, tooltip, this.area.getX() - 5, this.area.getY() + 2 + 12 * (selectedIndexInForLoop - tooltip.size() + 1));
                // CLIENT.currentScreen.renderTooltip(matrices, Texts.toText(tooltip), mouseX, mouseY);
                // suggestions$client.currentScreen.renderTooltip(matrices, Texts.toText(message), mouseX, mouseY);
                }
            }
        }
    }

    @Shadow
    public void select(int index) {}
}