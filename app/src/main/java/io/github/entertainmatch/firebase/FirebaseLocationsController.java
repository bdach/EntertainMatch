package io.github.entertainmatch.firebase;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.kelvinapps.rxfirebase.RxFirebaseDatabase;

import java.util.ArrayList;
import java.util.List;

import io.github.entertainmatch.firebase.models.FirebaseLocation;
import io.github.entertainmatch.firebase.models.FirebasePoll;
import io.github.entertainmatch.firebase.models.FirebaseUser;
import io.github.entertainmatch.model.EventDate;
import io.github.entertainmatch.model.Person;
import io.github.entertainmatch.utils.ListExt;
import rx.Observable;

/**
 * Created by Adrian Bednarz on 5/8/17.
 *
 * Retrieves information about locations
 */

public class FirebaseLocationsController {
    /**
     * Instance of the database
     */
    private static final FirebaseDatabase database = FirebaseDatabase.getInstance();
    /**
     * Holds reference to location collection.
     * In this collection each node is denoted by location id.
     */
    private static final DatabaseReference ref = database.getReference("locations");

    /**
     * Adds location information to the database
     * @param location Event to add
     */
    public static void add(FirebaseLocation location) {
        ref.child(location.getId()).setValue(location);
    }

    /**
     * Retrieves observable for all requested location by id.
     * @param locationId Location to get poll information for
     * @return Observable of location that evaluates only once.
     */
    public static Observable<FirebaseLocation> getLocationOnce(String locationId) {
        return RxFirebaseDatabase.observeSingleValueEvent(ref.child(locationId),
                        FirebaseLocation.class);
    }
}
