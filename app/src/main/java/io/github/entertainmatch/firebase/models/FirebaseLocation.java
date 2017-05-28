package io.github.entertainmatch.firebase.models;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

/**
 * Holds information about event's locations.
 * @author Adrian Bednarz
 * @since 5/8/17.
 */

@NoArgsConstructor
@AllArgsConstructor
@Getter
public class FirebaseLocation {
    /**
     * Unique location identifier.
     */
    private String id;
    /**
     * Name of the location where the event takes place.
     */
    private String place;
    /**
     * Latitude of the place location.
     */
    private Double lat;
    /**
     * Longtitude of the place location.
     */
    private Double lon;
}
