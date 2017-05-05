package io.github.entertainmatch.utils;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Function;

/**
 * Created by Adrian Bednarz on 4/30/17.
 *
 * Extensions of java.util.List class.
 * The main purpose is to avoid code duplication.
 * This is a place where we should implement all the neat functions that might make code more
 * readable. Some of the things that might occur there might be achievable using Java8.
 * Unfortunately e.g. Streams require Android API 24 what is just hilarious.
 */
public class ListExt {
    public static <T, U> List<T> map(List<U> that, Function<U, T> f) {
        List<T> result = new ArrayList<>(that.size());
        for (U item : that) {
            result.add(f.apply(item));
        }
        return result;
    }
}
