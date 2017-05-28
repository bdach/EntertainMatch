package io.github.entertainmatch.firebase.models;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

/**
 * Holds information about event dates.
 *
 * @author Adrian Bednarz
 * @since 5/8/17
 */
@NoArgsConstructor
@AllArgsConstructor
@Getter
public class FirebaseEventDate {
    /**
     * Identifier of category of this event date in database.
     */
    private String categoryId;

    /**
     * Identifier of an event.
     */
    private String eventId;

    /**
     * Identifier of event's location.
     */
    private String locationId;

    /**
     * Date timestamp of event.
     */
    private Long date;
}
