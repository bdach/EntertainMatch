package io.github.entertainmatch.firebase;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.kelvinapps.rxfirebase.DataSnapshotMapper;
import com.kelvinapps.rxfirebase.RxFirebaseDatabase;

import java.util.List;

import io.github.entertainmatch.firebase.models.FirebaseCategoryTemplate;
import rx.Observable;

/**
 * Created by Adrian Bednarz on 5/5/17.
 *
 * Manages templates for categoriesTemplates.
 */

public class FirebaseCategoriesTemplatesController {
    /**
     * Instance of the database
     */
    private static final FirebaseDatabase database = FirebaseDatabase.getInstance();
    /**
     * Holds reference to people collection.
     * In this collection each node is denoted by user's facebook id.
     */
    private static final DatabaseReference ref = database.getReference("categories");

    /**
     * Grabs user all category templates from firebase.
     * @return Observable to categoires provided by Firebase
     */
    public static Observable<List<FirebaseCategoryTemplate>> get() {
        return RxFirebaseDatabase.observeSingleValueEvent(
            ref,
            DataSnapshotMapper.listOf(FirebaseCategoryTemplate.class));
    }
}
