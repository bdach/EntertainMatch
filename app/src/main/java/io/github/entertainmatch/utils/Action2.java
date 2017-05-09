package io.github.entertainmatch.utils;

/**
 * @author Adrian Bednarz
 */
public interface Action2<U, T> {
    void perform(U object1, T object2);
}
