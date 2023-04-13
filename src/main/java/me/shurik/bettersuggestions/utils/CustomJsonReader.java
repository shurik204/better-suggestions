package me.shurik.bettersuggestions.utils;

import java.io.Reader;
import java.io.StringReader;

import com.google.gson.stream.JsonReader;

/**
 * Custom JsonReader that exposes the underlying reader.
 */
public class CustomJsonReader extends JsonReader {
    public Reader reader;
    private String input;

    public CustomJsonReader(String input) {
        this(new StringReader(input));
        this.input = input;
    }

    private CustomJsonReader(Reader in) {
        super(in);
        this.reader = in;
    }

    public String getInput() {
        return input;
    }

    public char getLastChar() {
        return input.charAt(input.length() - 1);
    }
    
    public char peekChar() {
        try {
            reader.mark(1);
            int c = reader.read();
            reader.reset();
            return (char) c;
        } catch (Exception e) {
            return 0;
        }
    }
}