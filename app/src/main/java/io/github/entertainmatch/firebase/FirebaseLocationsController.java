package io.github.entertainmatch.firebase;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.kelvinapps.rxfirebase.RxFirebaseDatabase;
import io.github.entertainmatch.firebase.models.FirebaseLocation;
import rx.Observable;

/**
 * Firebase controller used to retrieve {@link FirebaseLocation} data.
 *
 * @author Adrian Bednarz
 * @since 5/8/17
 */

public class FirebaseLocationsController {
    /**
     * Instance of the database.
     */
    private static final FirebaseDatabase database = FirebaseDatabase.getInstance();

    /**
     * Holds reference to location collection.
     * In this collection each node is denoted by location id.
     */
    private static final DatabaseReference ref = database.getReference("locations");

    /**
     * Retrieves an {@link Observable} for all requested location by ID.
     * @param locationId ID string of the location to get information for.
     * @return {@link Observable} yielding a single {@link FirebaseLocation}
     * object.
     */
    public static Observable<FirebaseLocation> getLocationOnce(String locationId) {
        return RxFirebaseDatabase.observeSingleValueEvent(
                ref.child(locationId),
                FirebaseLocation.class
        );
    }
}
