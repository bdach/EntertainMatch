package io.github.entertainmatch.utils;

import java.util.ArrayList;
import java.util.List;

import io.github.entertainmatch.model.Category;

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
    public static <T, U> ArrayList<T> map(List<U> that, Function<U, T> f) {
        ArrayList<T> result = new ArrayList<>(that.size());
        for (U item : that) {
            result.add(f.apply(item));
        }
        return result;
    }

    public static <T extends ICloneable<T>> ArrayList<T> clone(ArrayList<T> that) {
        ArrayList<T> result = new ArrayList<>(that.size());
        for (T item : that) {
            result.add(item.clone());
        }
        return result;
    }

    public static <T> boolean any(List<T> that, Function<T, Boolean> f) {
        for (T item : that) {
            if (f.apply(item))
                return true;
        }
        return false;
    }

    public static <T> void forEach(List<T> list, Action<T> action) {
        for (T item : list) {
            action.perform(item);
        }
    }
}
