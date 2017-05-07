package io.github.entertainmatch.utils;

import java.util.HashMap;

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

    public static <T> T getMax(HashMap<T, Long> tally) {
        T max = null;
        Long maxCount = Long.MIN_VALUE;
        for (HashMap.Entry<T, Long> entry : tally.entrySet()) {
            if (entry.getValue() > maxCount) {
                max = entry.getKey();
                maxCount = entry.getValue();
            }
        }
        return max;
    }
}
