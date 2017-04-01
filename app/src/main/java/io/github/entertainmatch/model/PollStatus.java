package io.github.entertainmatch.model;

import io.github.entertainmatch.R;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

/**
 * @author Bartlomiej Dach
 * @since 01.04.17
 */
@RequiredArgsConstructor
public enum PollStatus {
    CATEGORY    (R.string.poll_status_category),
    EVENT       (R.string.poll_status_event),
    DATE        (R.string.poll_status_date),
    COMPLETED   (R.string.poll_status_completed);

    @Getter
    private final int stringId;
}
