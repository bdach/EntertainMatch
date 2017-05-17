package io.github.entertainmatch.utils;

import android.text.TextUtils;

import java.util.*;

/**
 * Created by Adrian Bednarz on 5/6/17.
 *
 * HashMap convenient helpers.
 */

public class HashMapExt {
    public static <T, U> boolean all(HashMap<T, U> that, Function<U, Boolean> f) {
        for (U value : that.values()) {
            if (!f.apply(value))
                return false;
        }
        return true;
    }

    public static <T> List<T> getMax(HashMap<T, Long> tally) {
        List<T> max = new ArrayList<>();
        Long maxCount = Long.MIN_VALUE;
        for (HashMap.Entry<T, Long> entry : tally.entrySet()) {
            if (entry.getValue() > maxCount) {
                max.clear();
            }
            if (entry.getValue() >= maxCount) {
                max.add(entry.getKey());
                maxCount = entry.getValue();
            }
        }
        return max;
    }

    public static <T, U> List<U> mostFrequent(HashMap<T, U> that) {
        List<U> result = new ArrayList<>();
        int max = 1, counter = 1;
        U[] values = (U[]) that.values().toArray();

        if (values.length == 0)
            return null;

        Arrays.sort(values);

        for (int i = 0; i < values.length - 1; i++) {
            if (values[i].equals(values[i + 1])) {
                ++counter;
            } else {
                counter = 1;
            }

            if (counter > max)
                result.clear();

            if (counter >= max) {
                result.add(values[i]);
                max = counter;
            }
        }

        if (values.length >= 2 && max == 1 && !values[values.length - 2].equals(values[values.length - 1]))
            result.add(values[values.length - 1]);

        return result;
    }

    public static <T, U> void forEach(Map<T, U> map, Action2<T, U> action) {
        for (Map.Entry<T, U> entry : map.entrySet()) {
            action.perform(entry.getKey(), entry.getValue());
        }
    }
}
