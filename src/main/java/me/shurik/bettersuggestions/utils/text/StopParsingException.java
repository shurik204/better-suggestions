package me.shurik.bettersuggestions.utils.text;

import java.util.List;

import org.jetbrains.annotations.Nullable;

import me.shurik.bettersuggestions.utils.CompletionsContainer;
import me.shurik.bettersuggestions.utils.text.TextCompletions.TextCompletion;

/**
 * Utility exception to stop parsing and return to top level.
 * Optionally contains suggestions.
 */
public class StopParsingException extends RuntimeException {
    private static final long serialVersionUID = 1L;

    private CompletionsContainer<TextCompletion> suggestions;
    private String reason;

    public StopParsingException() {
        super("");
    }

    public StopParsingException(String reason, List<TextCompletion> suggestions) {
        this.suggestions = CompletionsContainer.of(suggestions);
        this.reason = reason;
    }

    public StopParsingException(String reason, int offset, TextCompletion... suggestions) {
        this.suggestions = CompletionsContainer.of(suggestions, offset);
        this.reason = reason;
    }

    public StopParsingException(String reason, int offset, List<TextCompletion> suggestions) {
        this.suggestions = CompletionsContainer.of(suggestions, offset);
        this.reason = reason;
    }

    @Nullable
    public CompletionsContainer<TextCompletion> getSuggestions() {
        return suggestions;
    }

    @Nullable
    public String getReason() {
        return reason;
    }
}