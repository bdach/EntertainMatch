package io.github.entertainmatch.firebase.models;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

/**
 * Created by Adrian Bednarz on 5/8/17.
 *
 * Holds information about event's locations.
 */

@NoArgsConstructor
@AllArgsConstructor
@Getter
public class FirebaseLocation {
    /**
     * Unique location identifier
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
