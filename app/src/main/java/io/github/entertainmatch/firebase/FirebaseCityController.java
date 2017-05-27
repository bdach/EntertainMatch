package io.github.entertainmatch.firebase;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.kelvinapps.rxfirebase.DataSnapshotMapper;
import com.kelvinapps.rxfirebase.RxFirebaseDatabase;
import rx.Observable;

import java.util.List;

/**
 * Controller responsible for fetching the list of cities available
 * for application users.
 *
 * @author Bartlomiej Dach
 * @since 25.05.17
 */
public class FirebaseCityController {
    /**
     * Database instance used.
     */
    private static final FirebaseDatabase database = FirebaseDatabase.getInstance();

    /**
     * Reference to the cities node.
     */
    private static final DatabaseReference ref = database.getReference("cities");

    /**
     * Returns an observable that will fetch the list of cities a single time.
     * @return An {@link Observable} yielding a {@link List} of {@link String}s
     * representing the city names.
     */
    public static Observable<List<String>> getCitiesOnce() {
        return RxFirebaseDatabase.observeSingleValueEvent(ref, DataSnapshotMapper.listOf(String.class));
    }

    /**
     * Returns an observable that will fetch the list of cities upon every update
     * in the database.
     * @return An {@link Observable} yielding a {@link List} of {@link String}s
     * representing the city names.
     */
    public static Observable<List<String>> getCities() {
        return RxFirebaseDatabase.observeValueEvent(ref, DataSnapshotMapper.listOf(String.class));
    }
}
