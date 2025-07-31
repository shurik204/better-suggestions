/*
package me.shurik.bettersuggestions.client.utils.text;

import com.google.gson.*;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.suggestion.SuggestionsBuilder;
import me.shurik.bettersuggestions.client.utils.CompletionsContainer;
import me.shurik.bettersuggestions.client.utils.text.TextCompletions.TextCompletion;
import me.shurik.bettersuggestions.utils.CustomJsonReader;
import net.minecraft.command.CommandSource;
import net.minecraft.text.Text;
import org.jetbrains.annotations.Nullable;

import java.io.EOFException;
import java.io.IOException;

public class TextParser {
    @Nullable
    public static <S> CompletionsContainer<TextCompletion> getCompletions(String json, CommandContext<S> context, SuggestionsBuilder builder) {
        try {
            // Try parsing the input as a Text object
            S source = context.getSource();
            if (source instanceof CommandSource cs) {
                Text.Serialization.fromJson(builder.getInput().substring(builder.getStart()), cs.getRegistryManager());
            }
            // if successful, don't suggest anything
            return null;
        } catch (Exception ignored) {}


        // net.minecraft.client.MinecraftClient client = net.minecraft.client.MinecraftClient.getInstance();
        try (CustomJsonReader jsonReader = new CustomJsonReader(json.trim())) {
            jsonReader.setLenient(false);

            readSomeValue(jsonReader);
            // client.player.sendMessage(Text.of("Parsed successfully!"), false);
        } catch (IOException e) {
            // client.inGameHud.getChatHud().addMessage(Text.of("IOException: " + e.getClass().getName() + " | " + e.getMessage()));
            // e.printStackTrace();
        } catch (StopParsingException e) {
            // client.inGameHud.getChatHud().addMessage(Text.of("Suggestions: " + e.getSuggestions()));
            // client.inGameHud.getChatHud().addMessage(Text.of("Reason: " + e.getReason()));
            // client.inGameHud.getChatHud().addMessage(Text.of("Offset: " + e.getSuggestions().getOffset()));
            return e.getSuggestions();
        }

        return null;
    }

    public static String readName(CustomJsonReader reader, JsonObject jsonObject) throws IOException {
        // System.out.println("readName");
        try {
            return reader.nextName();
        } catch (IOException e1) {
            // Try getting already typed input
            int offset = reader.getInput().lastIndexOf("\"");
            String input = offset != -1 ? reader.getInput().substring(offset) : "";

            throw new StopParsingException("End key name", input.length(), TextCompletions.keyCompletions(input, reader.getPath(), jsonObject));
        } catch (IllegalStateException e2) {
            throw new StopParsingException("End key name", TextCompletions.completions("\""));
        }
    }

    public static JsonElement readString(CustomJsonReader reader) {
        // System.out.println("readString");
        try {
            String string = reader.nextString();
            return new JsonPrimitive(string);
        } catch (IllegalStateException | IOException e) {
            String lastKey = reader.getPath().substring(reader.getPath().lastIndexOf(".") + 1);
            String input = reader.getInput().lastIndexOf("\"") != -1 ? reader.getInput().substring(reader.getInput().lastIndexOf("\"")) : "";

            throw new StopParsingException("End string value", input.length(), TextCompletions.valueCompletions(lastKey, reader.getPath(), input));
        }
    }

    public static JsonObject readObject(CustomJsonReader reader) throws IOException {
        // System.out.println("readObject");
        reader.beginObject();

        JsonObject jsonObject = new JsonObject();

        try {
            while (reader.hasNext()) {
                try {
                    // Read the key
                    // If there's an IOException, try skipping the value
                    String key = readName(reader, jsonObject);

                    // If reader.peek() produces EOF, suggest a colon
                    try {
                        reader.peek();
                    } catch (EOFException e1) {
                        if (!(reader.getLastChar() == ':'))
                            throw new StopParsingException("Colon separating name and value", TextCompletions.completions(":"));
                        else {
                            // If there's a colon, suggest a value
                            // Grab the input after the colon
                            int offset = reader.getInput().lastIndexOf("\":");
                            String input = reader.getInput().substring(offset + 2);
                            throw new StopParsingException("Complete value", input.length(), TextCompletions.valueCompletions(key, reader.getPath(), input));
                        }
                    }
                    try {
                        JsonElement value = readSomeValue(reader);
                        jsonObject.add(key, value);
                    } catch (EOFException e2) {
                        throw new StopParsingException("End object", TextCompletions.completions("}"));
                    }
                } catch (IOException e3) {
                    int offset = reader.getInput().lastIndexOf(":");
                    String input = offset != -1 ? reader.getInput().substring(offset + 1) : "";
                    if ("true".startsWith(input) || "false".startsWith(input)) {
                        throw new StopParsingException("Complete boolean | IOException", input.length(), TextCompletions.booleanCompletions(input));
                    }
                    reader.skipValue();
                }
            }
        } catch (EOFException e) {
            if (reader.getLastChar() == '{' || reader.getLastChar() == ',')
                throw new StopParsingException("Start a key name", 0, TextCompletions.keyCompletions("", reader.getPath(), jsonObject));
            else
                throw new StopParsingException("End object | EOFException", TextCompletions.completions("}"));
        }

        try {
            reader.endObject();
        } catch (EOFException e) {
            throw new StopParsingException("End object | reader.endObject()", TextCompletions.completions("}"));
        }
        return jsonObject;
    }


    public static JsonArray readArray(CustomJsonReader reader) throws IOException {
        // System.out.println("readArray");
        reader.beginArray();
        JsonArray jsonArray = new JsonArray();

        try {
            while (reader.hasNext()) {
                try {
                    JsonElement value = readSomeValue(reader);
                    jsonArray.add(value);
                } catch (IOException e) {
                    reader.skipValue();
                }
            }
        } catch (EOFException e) {
            if (reader.getLastChar() == '[' || reader.getLastChar() == ',')
                // Suggest opening an object or a string
                throw new StopParsingException("Start a new object or string | EOFException", TextCompletions.completions("{", "\""));
            else
                // Suggest closing the array
                throw new StopParsingException("End array | EOFException",TextCompletions.completions("]"));
        }

        reader.endArray();
        return jsonArray;
    }

    public static JsonPrimitive readBoolean(CustomJsonReader reader) {
        // System.out.println("readBoolean");

        try {
            return new JsonPrimitive(reader.nextBoolean());
        } catch (IllegalStateException | IOException e) {
            // Try finding the beginning of the boolean.
            // It could be either after an opening bracket or a comma.
            int substringIndex = reader.getInput().lastIndexOf("[") != -1 ? reader.getInput().lastIndexOf("[") + 1 : (reader.getInput().lastIndexOf(":") != 1 ? reader.getInput().lastIndexOf(":") + 1 : -1);
            if (substringIndex != -1) {
                throw new StopParsingException("Finish boolean", substringIndex, TextCompletions.matchingCompletions(reader.getInput().substring(substringIndex), "true", "false"));
            } else {
                throw new StopParsingException("Finish boolean", TextCompletions.completions("true", "false"));
            }
        }
    }

    public static JsonPrimitive readNumber(CustomJsonReader reader) {
        // System.out.println("readNumber");
        try {
            return new JsonPrimitive(reader.nextDouble());
        } catch (IllegalStateException | IOException e) {
            throw new StopParsingException();
        }
    }

    public static JsonElement readSomeValue(CustomJsonReader reader) throws IOException {
        // System.out.println("readSomeValue");
        return switch (reader.peek()) {
            case STRING -> readString(reader);
            case BEGIN_OBJECT -> readObject(reader);
            case BEGIN_ARRAY -> readArray(reader);
            case NUMBER -> readNumber(reader);
            case BOOLEAN -> readBoolean(reader);
            case NULL -> {
                reader.nextNull();
                yield JsonNull.INSTANCE;
            }
            default -> throw new StopParsingException();
        };
    }
}
*/