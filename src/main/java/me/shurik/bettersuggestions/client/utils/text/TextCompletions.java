package me.shurik.bettersuggestions.client.utils.text;

import com.google.common.collect.Lists;
import com.google.gson.JsonObject;
import me.shurik.bettersuggestions.client.mixin.TranslationStorageAccessorMixin;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.option.KeyBinding;
import net.minecraft.util.Language;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.hit.HitResult;
import net.minecraft.util.math.BlockPos;

import java.util.Arrays;
import java.util.List;
import java.util.Set;

public class TextCompletions {
    public record TextCompletion (String value) {}
    
    public static List<TextCompletion> completions(String... values) {
        return Arrays.stream(values).map(TextCompletion::new).toList();
    }

    public static List<TextCompletion> matchingCompletions(String input, String... values) {
        return Arrays.stream(values).filter(value -> value.startsWith(input)).map(TextCompletion::new).toList();
    }

    public static List<TextCompletion> matchingCompletions(String input, List<String> values) {
        return values.stream().filter(value -> value.startsWith(input)).map(TextCompletion::new).toList();
    }
    
    public static List<TextCompletion> matchingWithQuoteCompletions(String input, String... values) {
        return Arrays.stream(values).filter(value -> value.substring(1).startsWith(input)).map(TextCompletion::new).toList();
    }

    public static List<TextCompletion> matchingWithQuoteCompletions(String input, List<String> values) {
        return values.stream().filter(value -> value.substring(1).startsWith(input)).map(TextCompletion::new).toList();
    }

    public static List<TextCompletion> matchingCompletions(String input, List<String> values, Set<String> exclude) {
        // TODO: Doesn't work
        List<String> filtered = Lists.newArrayList(values);
        filtered.removeAll(exclude);
        return filtered.stream().filter(value -> (value.startsWith(input))).map(TextCompletion::new).toList();
    }

    public static final List<String> KEYS = Lists.newArrayList("text", "color", "bold", "italic", "underlined", "strikethrough", "obfuscated", "insertion", "clickEvent", "hoverEvent", "extra", "nbt", "storage", "entity", "block", "translate", "with", "score", "selector", "keybind", "font", "interpret");
    public static final List<String> EVENT_KEYS = Lists.newArrayList("action", "value");
    public static final List<String> SCORE_KEYS = Lists.newArrayList("name", "objective", "value");
    
    public static final List<String> AUTOCOMPLETE_KEYS = KEYS.stream().map(key -> "\"" + key + "\"").toList();
    public static final List<String> AUTOCOMPLETE_EVENT_KEYS = EVENT_KEYS.stream().map(key -> "\"" + key + "\"").toList();
    public static final List<String> AUTOCOMPLETE_SCORE_KEYS = SCORE_KEYS.stream().map(key -> "\"" + key + "\"").toList();

    public static List<TextCompletion> keyCompletions(String input, String jsonPath, JsonObject jsonObject) {
        String[] path = jsonPath.split("\\.");
        // Only suggest keys if the last character is a dot
        if ((path[path.length - 1].equals("score") && jsonPath.endsWith(".")) || (path.length > 1 && path[path.length - 2].equals("score"))) {
            return matchingCompletions(input, AUTOCOMPLETE_SCORE_KEYS, jsonObject.keySet());
        } else if (
            ((path[path.length - 1].equals("clickevent") || path[path.length - 1].equals("hoverevent")) && jsonPath.endsWith("."))
            || (path.length > 1 && (path[path.length - 2].equals("clickevent") || path[path.length - 2].equals("hoverevent")))) {
            return matchingCompletions(input, AUTOCOMPLETE_EVENT_KEYS, jsonObject.keySet());
        } else {
            return matchingCompletions(input, AUTOCOMPLETE_KEYS, jsonObject.keySet());
        }
    }

    public static List<TextCompletion> valueCompletions(String key, String jsonPath, String input) {
        if (input.isEmpty() && (key.equals("with") || key.equals("extra")))
            return completions("[");
        
        if (input.isEmpty() && (key.equals("score") || key.equals("clickevent") || key.equals("hoverevent")))
            return completions("{");

        switch (key) {
            case "color" -> {
                if (!input.endsWith("\""))
                    return completions("\"");
                input = input.substring(0, input.length() - 1);
                return colorCompletions(input);
            }
            case "action" -> {
                if (!input.endsWith("\""))
                    return completions("\"");
                String[] path = jsonPath.split("\\.");
                if (path[path.length - 2].equals("clickevent")) {
                    input = input.substring(0, input.length() - 1);
                    return clickActionCompletions(input);
                } else if (path[path.length - 2].equals("hoverevent")) {
                    input = input.substring(0, input.length() - 1);
                    return hoverActionCompletions(input);
                } else {
                    return Lists.newArrayList();
                }
            }
            case "bold", "italic", "underlined", "strikethrough", "obfuscated", "interpret" -> {
                return booleanCompletions(input);
            }
            case "keybind" -> {
                return keybindCompletions(input);
            }
            case "block" -> {
                return blockCompletions(input);
            }
            case "translate" -> {
                return translationCompletions(input);
            }
            default -> {
                return completions(input + "\"");
            }
        }
    }

    public static final List<String> COLORS = Lists.newArrayList("black", "dark_blue", "dark_green", "dark_aqua", "dark_red", "dark_purple", "gold", "gray", "dark_gray", "blue", "green", "aqua", "red", "light_purple", "yellow", "white", "reset");
    public static final List<String> AUTOCOMPLETE_COLORS = COLORS.stream().map(color -> "\"" + color + "\"").toList();
    public static List<TextCompletion> colorCompletions(String input) {
        return matchingCompletions(input, AUTOCOMPLETE_COLORS);
    }

    public static final List<String> CLICK_ACTIONS = Lists.newArrayList("open_url", "run_command", "suggest_command", "change_page", "copy_to_clipboard");
    public static final List<String> AUTOCOMPLETE_CLICK_ACTIONS = CLICK_ACTIONS.stream().map(action -> "\"" + action + "\"").toList();
    public static List<TextCompletion> clickActionCompletions(String input) {
        return matchingCompletions(input, AUTOCOMPLETE_CLICK_ACTIONS);
    }

    public static final List<String> HOVER_ACTIONS = Lists.newArrayList("show_text", "show_item", "show_entity", "show_achievement");
    public static final List<String> AUTOCOMPLETE_HOVER_ACTIONS = HOVER_ACTIONS.stream().map(action -> "\"" + action + "\"").toList();
    public static List<TextCompletion> hoverActionCompletions(String input) {
        return matchingCompletions(input, AUTOCOMPLETE_HOVER_ACTIONS);
    }

    public static final List<String> BOOLEAN = Lists.newArrayList("true", "false");
    public static List<TextCompletion> booleanCompletions(String input) {
        return matchingCompletions(input, BOOLEAN);
    }

    public static final List<String> TRANSLATION_CACHE = Lists.newArrayList();
    public static List<TextCompletion> translationCompletions(String input) {
        if (TRANSLATION_CACHE.isEmpty()) {
            Set<String> keys = ((TranslationStorageAccessorMixin) Language.getInstance()).getTranslations().keySet();
            keys.forEach((key) -> TRANSLATION_CACHE.add("\"" + key + "\""));
        }

        return matchingCompletions(input, TRANSLATION_CACHE);
    }

    public static final List<String> KEYBIND_CACHE = Lists.newArrayList();
    public static List<TextCompletion> keybindCompletions(String input) {
        MinecraftClient client = MinecraftClient.getInstance();
        if (client == null) return Lists.newArrayList();

        if (KEYBIND_CACHE.isEmpty()) {
            for (KeyBinding allKeys : client.options.allKeys) {
                KEYBIND_CACHE.add("\"" + allKeys.getTranslationKey() + "\"");
            }
        }
        
        return matchingCompletions(input, KEYBIND_CACHE);
    }

    public static List<TextCompletion> blockCompletions(String input) {
        if (!input.endsWith("\""))
            return Lists.newArrayList(completions("\""));

        MinecraftClient client = MinecraftClient.getInstance();
        if (client == null) return Lists.newArrayList();

        input = input.substring(0, input.length() - 1);

        if (client.crosshairTarget != null && client.crosshairTarget.getType() == HitResult.Type.BLOCK) {
            BlockHitResult blockHitResult = (BlockHitResult) client.crosshairTarget;
            BlockPos pos = blockHitResult.getBlockPos();
            
            return Lists.newArrayList(matchingCompletions(input, "\"" + pos.getX(), "\"" + pos.getX() + " " + pos.getY(), "\"" + pos.getX() + " " + pos.getY() + " " + pos.getZ()));
        } else {
            return Lists.newArrayList(matchingCompletions(input, "\"~", "\"~ ~", "\"~ ~ ~"));
        }
    }
}
