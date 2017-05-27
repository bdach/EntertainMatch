package io.github.entertainmatch.firebase;

import android.util.Log;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.kelvinapps.rxfirebase.DataSnapshotMapper;
import com.kelvinapps.rxfirebase.RxFirebaseDatabase;

import java.util.List;
import java.util.Map;

import io.github.entertainmatch.firebase.models.FirebaseCategoryTemplate;
import io.github.entertainmatch.model.Category;
import io.github.entertainmatch.model.ConcertEvent;
import io.github.entertainmatch.model.Event;
import io.github.entertainmatch.model.MovieEvent;
import io.github.entertainmatch.model.PlayEvent;
import io.github.entertainmatch.model.StaffPickEvent;
import io.github.entertainmatch.utils.ListExt;
import lombok.Getter;
import rx.Observable;

/**
 * Created by Adrian Bednarz on 4/12/17.
 */

public class FirebaseEventController {
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
            FirebaseCategoriesTemplatesController.cached = ListExt.map(x, FirebaseCategoryTemplate::toCategory);
            FirebaseCategoriesTemplatesController.setCachedMap(
                    ListExt.toMap(FirebaseCategoriesTemplatesController.cached, Category::getId));
            Log.d("FirebaseEventController", "categoriesTemplates ready " + FirebaseCategoriesTemplatesController.cached.size());
        });
    }

    public static Observable<Map<String, ? extends Event>> getEventsSingle(String city, String category) {
        Class<? extends Event> eventClass = getClassForCategory(category);

        return RxFirebaseDatabase.observeSingleValueEvent(
                ref.child(city).child(category),
                DataSnapshotMapper.mapOf(eventClass)
        );
    }

    public static Observable<? extends Event> getEventSingle(String chosenCategory, String victoriousEvent) {
        Class<? extends Event> eventClass = getClassForCategory(chosenCategory);
        return RxFirebaseDatabase.observeValueEvent(ref.child(victoriousEvent), eventClass);
    }

    static Class<? extends Event> getClassForCategory(String category) {
        Class<? extends Event> eventClass;
        switch (category) {
            case "movies":
                eventClass = MovieEvent.class;
                break;
            case "concerts":
                eventClass = ConcertEvent.class;
                break;
            case "plays":
                eventClass = PlayEvent.class;
                break;
            case "staffpicks":
                eventClass = StaffPickEvent.class;
                break;
            default:
                throw new IllegalArgumentException("This type of category has not been implemented yet");
        }
        return eventClass;
    }
}