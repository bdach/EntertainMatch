package io.github.entertainmatch.utils;

/**
 * @author Bartlomiej Dach
 * @since 06.05.17
 */
public interface Action<U> {
    void perform(U object1);
}

