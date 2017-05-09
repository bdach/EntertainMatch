package io.github.entertainmatch.firebase;

import android.graphics.Movie;
import android.util.Log;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.kelvinapps.rxfirebase.DataSnapshotMapper;
import com.kelvinapps.rxfirebase.RxFirebaseDatabase;

import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.function.Consumer;

import io.github.entertainmatch.facebook.FacebookUsers;
import io.github.entertainmatch.firebase.models.FirebaseCategoryTemplate;
import io.github.entertainmatch.firebase.models.FirebaseEventDate;
import io.github.entertainmatch.firebase.models.FirebaseLocation;
import io.github.entertainmatch.firebase.models.FirebasePoll;
import io.github.entertainmatch.model.ConcertEvent;
import io.github.entertainmatch.model.Event;
import io.github.entertainmatch.model.EventDate;
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
    private static final Observable<List<? extends Event>> movieEventsObservable;

    static {
        movieEventsObservable = read();
    }

    private static void save(List<? extends Event> list) {
        ref.setValue(list);
    }

    private static Observable<List<? extends Event>> read() {
        return RxFirebaseDatabase.observeValueEvent(ref.child("movies"), DataSnapshotMapper.listOf(MovieEvent.class));
    }

    public static void init() {
        // force to call static constructor
        FirebaseCategoriesTemplatesController.get().subscribe((List<FirebaseCategoryTemplate> x) -> {
            VoteCategoryStage.categoriesTemplates = ListExt.map(x, FirebaseCategoryTemplate::toCategory);
            Log.d("FirebaseController", "categoriesTemplates ready " + VoteCategoryStage.categoriesTemplates.size());
        });
    }

    public static Observable<List<? extends Event>> getEventsObservable(String chosenCategory) {
        Class<? extends Event> eventClass = getClassForCategory(chosenCategory);

        return RxFirebaseDatabase.observeValueEvent(ref.child(chosenCategory), DataSnapshotMapper.listOf(eventClass));
    }

    public static Observable<? extends Event> getEventSingle(String chosenCategory, String victoriousEvent) {
        Class<? extends Event> eventClass = getClassForCategory(chosenCategory);

        return RxFirebaseDatabase.observeValueEvent(ref.child(chosenCategory).child(victoriousEvent), eventClass);
    }

    private static Class<? extends Event> getClassForCategory(String category) {
        Class<? extends Event> eventClass;
        switch (category) {
            case "movies":
                eventClass = MovieEvent.class;
                break;
            case "concerts":
                eventClass = ConcertEvent.class;
                break;
            default:
                throw new IllegalArgumentException("This type of category has not been implemented yet");
        }
        return eventClass;
    }
}