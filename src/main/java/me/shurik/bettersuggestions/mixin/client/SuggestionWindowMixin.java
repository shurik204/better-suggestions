package me.shurik.bettersuggestions.mixin.client;

import static me.shurik.bettersuggestions.BetterSuggestionsMod.CONFIG;
import static me.shurik.bettersuggestions.BetterSuggestionsModClient.CLIENT;

import java.util.ArrayList;
import java.util.List;
import org.lwjgl.glfw.GLFW;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Overwrite;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import com.mojang.brigadier.context.StringRange;
import com.mojang.brigadier.suggestion.Suggestion;

import me.shurik.bettersuggestions.access.CustomSuggestionAccessor;
import me.shurik.bettersuggestions.access.HighlightableEntityAccessor;
import me.shurik.bettersuggestions.utils.ClientUtils;
import me.shurik.bettersuggestions.utils.RegistryUtils;
import net.minecraft.client.gui.DrawableHelper;
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
 * Sort suggestions
 */
@Mixin(SuggestionWindow.class)
public class SuggestionWindowMixin {
    private final int maxSuggestionsShown = CONFIG.maxSuggestionsShown;
    private int color;

    private ChatInputSuggestorAccessorMixin suggestorAccessor;

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
        this.color = suggestor.color;

        this.suggestorAccessor = (ChatInputSuggestorAccessorMixin) suggestor;
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

        int inputLength = suggestorAccessor.getTextField().getText().length();
        
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

    /**
     * @author shurik204
     * @reason There's a better way to do this by injecting near the `boolean bl5 = false;` line, but I counldn't get it to work.
     */
    // @Overwrite
    @Inject(at = @At("HEAD"), method = "render", cancellable = true)
    public void render(MatrixStack matrices, int mouseX, int mouseY, CallbackInfo info) {
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
                CLIENT.currentScreen.renderTooltip(matrices, tooltip, mouseX, mouseY);
            }
        } else {
            if (Screen.hasShiftDown()) {
                List<Text> tooltip = ((CustomSuggestionAccessor)this.suggestions.get(this.selection)).getMultilineTooltip();
                if (tooltip != null) {
                    CLIENT.currentScreen.renderTooltip(matrices, tooltip, this.area.getX() - 5, this.area.getY() + 2 + 12 * (selectedIndexInForLoop - tooltip.size() + 1));
                }
            }
        }

        info.cancel();
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