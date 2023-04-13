package me.shurik.bettersuggestions.utils;

import java.util.ArrayList;
import java.util.List;

public class CompletionsContainer<T> extends ArrayList<T> {
    private int offset = 0;

    public CompletionsContainer() {
        super();
    }

    public CompletionsContainer(int initialCapacity) {
        super(initialCapacity);
    }

    public CompletionsContainer(Iterable<? extends T> c) {
        this(c, 0);
    }

    public CompletionsContainer(Iterable<? extends T> c, int offset) {
        super();
        this.offset = offset;
        for (T t : c) {
            add(t);
        }
    }

    public int getOffset() {
        return offset;
    }

    public static <T> CompletionsContainer<T> empty() {
        return new CompletionsContainer<T>();
    }

    public static <T> CompletionsContainer<T> of(Iterable<? extends T> elements, int offset) {
        return new CompletionsContainer<T>(elements, offset);
    }

    public static <T> CompletionsContainer<T> of(List<T> elements) {
        return new CompletionsContainer<T>(elements);
    }

    public static <T> CompletionsContainer<T> of(List<T> elements, int offset) {
        return new CompletionsContainer<T>(elements, offset);
    }

    public static <T> CompletionsContainer<T> of(T[] elements, int offset) {
        CompletionsContainer<T> list = new CompletionsContainer<T>(elements.length);
        for (T t : elements) {
            list.add(t);
        }
        return list;
    }
}
