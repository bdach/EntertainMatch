package io.github.entertainmatch.utils;

/**
 * Created by Adrian Bednarz on 5/8/17.
 *
 * Two argument action.
 */
public interface Action2<U, T> {
    void perform(U object1, T object2);
}
