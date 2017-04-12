package io.github.entertainmatch.firebase;

import android.util.Log;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.kelvinapps.rxfirebase.DataSnapshotMapper;
import com.kelvinapps.rxfirebase.RxFirebaseDatabase;

import java.util.List;
import java.util.logging.Logger;

import io.github.entertainmatch.model.MovieEvent;
import lombok.Getter;
import rx.Observable;

/**
 * Created by Adrian Bednarz on 4/12/17.
 */

public class FirebaseController {
    private static final FirebaseDatabase database = FirebaseDatabase.getInstance();
    private static final DatabaseReference ref = database.getReference("list");
    @Getter
    private static final Observable<List<MovieEvent>> movieEventsObservable;

    static {
        movieEventsObservable = read();
        Log.d("WTF", "A");
    }

    private static void save(List<MovieEvent> list) {
        ref.setValue(list);
    }

    private static Observable<List<MovieEvent>> read() {
        return RxFirebaseDatabase.observeValueEvent(ref, DataSnapshotMapper.listOf(MovieEvent.class));
    }

    public static void init() {
        // force to call static constructor
    }
}