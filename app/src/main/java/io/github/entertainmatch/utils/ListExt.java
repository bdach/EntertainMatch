package io.github.entertainmatch.utils;

import java.util.*;

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

    public static <T, U> void zippedForeach(List<T> lhs, List<U> rhs, Action2<T, U> action) {
        int len = Math.min(lhs.size(), rhs.size());
        for (int i = 0; i < len; i++)
            action.perform(lhs.get(i), rhs.get(i));
    }

    public static <K, V> Map<K, V> toMap(List<V> elements, Function<V, K> selector) {
        Map<K, V> result = new HashMap<>();
        for (V elem : elements) {
            result.put(selector.apply(elem), elem);
        }
        return result;
    }

    public static <T> Boolean removeIf(ArrayList<T> list, Function<T, Boolean> predicate) {
        boolean changed = false;
        for (Iterator<T> iterator = list.iterator(); iterator.hasNext(); ) {
            T item = iterator.next();
            if (predicate.apply(item)) {
                list.remove(item);
                changed = true;
            }
        }
        return changed;
    }
}
