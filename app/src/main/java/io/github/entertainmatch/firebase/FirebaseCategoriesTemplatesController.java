package io.github.entertainmatch.firebase;

import android.util.Log;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.kelvinapps.rxfirebase.DataSnapshotMapper;
import com.kelvinapps.rxfirebase.RxFirebaseDatabase;
import io.github.entertainmatch.firebase.models.FirebaseCategoryTemplate;
import io.github.entertainmatch.model.Category;
import io.github.entertainmatch.utils.ListExt;
import lombok.Getter;
import lombok.Setter;
import rx.Observable;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Firebase controller responsible for handling {@link Category} templates.
 *
 * @author Adrian Bednarz
 * @since 5/5/17
 */

public class FirebaseCategoriesTemplatesController {
    /**
     * Instance of the database.
     */
    private static final FirebaseDatabase database = FirebaseDatabase.getInstance();

    /**
     * Holds reference to people collection.
     * In this collection each node is denoted by user's facebook id.
     */
    private static final DatabaseReference ref = database.getReference("categories");

    /**
     * Since these templates are not going to change, they are cached and loaded on startup.
     */
    @Getter
    @Setter
    static List<Category> cached = new ArrayList<>();

    /**
     * Since these templates are not going to change, they are cached and loaded on startup.
     */
    @Getter
    @Setter
    static Map<String, Category> cachedMap = new HashMap<>();

    /**
     * Grabs user all available {@link FirebaseCategoryTemplate}s from Firebase.
     * @return An {@link Observable} yielding a {@link List} of
     * {@link FirebaseCategoryTemplate}s provided by Firebase.
     */
    public static Observable<List<FirebaseCategoryTemplate>> get() {
        return RxFirebaseDatabase.observeSingleValueEvent(
            ref,
            DataSnapshotMapper.listOf(FirebaseCategoryTemplate.class)
        );
    }

    /**
     * Fetches an image URL for a category template.
     * @param categoryId ID string of the category template.
     * @return Image URL for the template with the supplied ID.
     */
    static String getImageForCategory(String categoryId) {
        return cachedMap.get(categoryId).getImageUrl();
    }

    /**
     * Returns a map of {@link FirebaseCategoryTemplate}s, indexed by
     * category ID.
     * @return A {@link Map} of all available {@link FirebaseCategoryTemplate}s
     * indexed by category ID.
     */
    public static Observable<Map<String, FirebaseCategoryTemplate>> getMap() {
        return RxFirebaseDatabase.observeSingleValueEvent(
                ref,
                DataSnapshotMapper.listOf(FirebaseCategoryTemplate.class)
        ).map(templates -> ListExt.toMap(templates, FirebaseCategoryTemplate::getId));
    }

    /**
     * Initializes static fields. Used when starting the app.
     */
    public static void init() {
        // force to call static constructor
        get().subscribe((List<FirebaseCategoryTemplate> x) -> {
            cached = ListExt.map(x, FirebaseCategoryTemplate::toCategory);
            setCachedMap(
                    ListExt.toMap(cached, Category::getId));
            Log.d("FirebaseCategories", "categoriesTemplates ready " + cached.size());
        });
    }
}
