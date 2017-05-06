package io.github.entertainmatch.utils;

/**
 * @author Bartlomiej Dach
 * @since 06.05.17
 */
public interface Function<U, T> {
    T apply(U item);
}
