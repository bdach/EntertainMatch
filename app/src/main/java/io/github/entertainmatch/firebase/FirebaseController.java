package io.github.entertainmatch.firebase;

import android.net.Uri;
import android.util.Log;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.kelvinapps.rxfirebase.DataSnapshotMapper;
import com.kelvinapps.rxfirebase.RxFirebaseDatabase;

import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;

import io.github.entertainmatch.firebase.models.FirebaseCategoryTemplate;
import io.github.entertainmatch.model.Category;
import io.github.entertainmatch.model.MovieEvent;
import io.github.entertainmatch.model.VoteCategoryStage;
import io.github.entertainmatch.utils.ListExt;
import lombok.Getter;
import rx.Observable;

/**
 * Created by Adrian Bednarz on 4/12/17.
 */

public class FirebaseController {
    /**
     * Instance of the database
     */
    private static final FirebaseDatabase database = FirebaseDatabase.getInstance();

    /**
     * Holds reference to movies collection.
     */
    private static final DatabaseReference ref = database.getReference("events");

    /**
     * Returns observable collection of currently available movies.
     */
    @Getter
    private static final Observable<List<MovieEvent>> movieEventsObservable;

    static {
        movieEventsObservable = read();
    }

    private static void save(List<MovieEvent> list) {
        ref.setValue(list);
    }

    private static Observable<List<MovieEvent>> read() {
        return RxFirebaseDatabase.observeValueEvent(ref.child("movies"), DataSnapshotMapper.listOf(MovieEvent.class));
    }

    public static void init() {
        // force to call static constructor
        FirebaseCategoriesTemplatesController.get().subscribe(x -> {
            VoteCategoryStage.categories = ListExt.map(x, FirebaseCategoryTemplate::toCategory);
            Log.d("FirebaseController", "categories ready");
        });
    }
}