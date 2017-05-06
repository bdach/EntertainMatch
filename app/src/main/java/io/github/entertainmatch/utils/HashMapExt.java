package io.github.entertainmatch.utils;

import java.util.HashMap;
import java.util.function.Function;

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
}
