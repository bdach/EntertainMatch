package io.github.entertainmatch.firebase;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.kelvinapps.rxfirebase.DataSnapshotMapper;
import com.kelvinapps.rxfirebase.RxFirebaseDatabase;
import rx.Observable;

import java.util.List;

/**
 * @author Bartlomiej Dach
 * @since 25.05.17
 */
public class FirebaseCityController {
    private static final FirebaseDatabase database = FirebaseDatabase.getInstance();
    private static final DatabaseReference ref = database.getReference("cities");

    public static Observable<List<String>> getCitiesOnce() {
        return RxFirebaseDatabase.observeSingleValueEvent(ref, DataSnapshotMapper.listOf(String.class));
    }

    public static Observable<List<String>> getCities() {
        return RxFirebaseDatabase.observeValueEvent(ref, DataSnapshotMapper.listOf(String.class));
    }
}
