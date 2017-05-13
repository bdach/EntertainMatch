package io.github.entertainmatch.reactive.ui.models;

import lombok.Getter;

/**
 * Created by Adrian Bednarz on 5/13/17.
 */

@Getter
public class UiModel<T> {
    final boolean inProgress;
    final boolean success;
    final String errorMessage;
    final T value;

    protected UiModel(boolean inProgress, boolean success, String errorMessage, T value) {
        this.inProgress = inProgress;
        this.success = success;
        this.errorMessage = errorMessage;
        this.value = value;
    }

    public static <T> UiModel<T> inProgress() {
        return new UiModel<>(true, false, null, null);
    }

    public static <T> UiModel<T> error(String errorMessage) {
        return new UiModel<>(false, false, errorMessage, null);
    }

    public static <T> UiModel<T> result(T value) {
        return new UiModel<>(false, true, null, value);
    }

    public static <T> UiModel<T> idle() {
        return new UiModel<>(false, false, null, null);
    }
}
