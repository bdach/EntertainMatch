package io.github.entertainmatch.utils;

import android.text.TextUtils;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

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

    public static <T, U> U mostFrequent(HashMap<T, U> that) {
        int max = 0, counter = 0;
        U[] values = (U[]) that.values().toArray();

        if (values.length == 0)
            return null;

        U mostFreq = values[0];
        Arrays.sort(values);

        for (int i = 0; i < values.length - 1; i++) {
            if (values[i].equals(values[i + 1])) {
                ++counter;
            } else {
                counter = 1;
            }

            if (counter >= max) {
                max = counter;
                mostFreq = values[i];
            }
        }

        return mostFreq;
    }
}
