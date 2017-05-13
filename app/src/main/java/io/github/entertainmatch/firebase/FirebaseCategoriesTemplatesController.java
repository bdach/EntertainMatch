package io.github.entertainmatch.firebase;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.kelvinapps.rxfirebase.DataSnapshotMapper;
import com.kelvinapps.rxfirebase.RxFirebaseDatabase;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import io.github.entertainmatch.firebase.models.FirebaseCategoryTemplate;
import io.github.entertainmatch.model.Category;
import io.github.entertainmatch.utils.HashMapExt;
import lombok.Getter;
import lombok.Setter;
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
     * Grabs user all category templates from firebase.
     * @return Observable to categoires provided by Firebase
     */
    public static Observable<List<FirebaseCategoryTemplate>> get() {
        return RxFirebaseDatabase.observeSingleValueEvent(
            ref,
            DataSnapshotMapper.listOf(FirebaseCategoryTemplate.class));
    }
}
