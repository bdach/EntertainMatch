package io.github.entertainmatch.firebase.models;

import java.util.Date;
import java.util.Map;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

/**
 * Created by Adrian Bednarz on 5/8/17.
 *
 * Holds information about event dates.
 */
@NoArgsConstructor
@AllArgsConstructor
@Getter
public class FirebaseEventDate {
    /**
     * Identifier of category of this event date in database
     */
    private String categoryId;
    /**
     * Identifier of an event
     */
    private String eventId;
    /**
     * Identifier of event's location
     */
    private String locationId;
    /**
     * Date timestamp of event
     */
    private Long date;
}
