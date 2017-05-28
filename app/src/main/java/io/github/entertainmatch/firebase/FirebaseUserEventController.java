package io.github.entertainmatch.firebase;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.kelvinapps.rxfirebase.RxFirebaseDatabase;
import io.github.entertainmatch.firebase.models.FirebaseCompletedPoll;
import io.github.entertainmatch.firebase.models.FirebaseUser;
import rx.Observable;

/**
 * Responsible for fetching and managing lists of {@link FirebaseCompletedPoll}s
 * for users.
 *
 * @author Bartlomiej Dach
 * @since 16.05.17
 */
public class FirebaseUserEventController {
    /**
     * Database instance used.
     */
    private static final FirebaseDatabase database = FirebaseDatabase.getInstance();

    /**
     * Target database reference.
     */
    private static final DatabaseReference ref = database.getReference("user_events");

    /**
     * Returns an {@link Observable} that returns updated {@link FirebaseCompletedPoll}
     * instances.
     * @param facebookId ID of the user for whom to fetch completed polls.
     * @return An {@link Observable} yielding {@link FirebaseCompletedPoll} objects.
     */
    public static Observable<FirebaseCompletedPoll> getEventsForUser(String facebookId) {
        return RxFirebaseDatabase.observeValueEvent(ref.child(facebookId), FirebaseUser.class)
                .flatMap(user -> Observable.merge(FirebaseCompletedPollController.getPollsForUser(user)));
    }

    /**
     * Adds a finished poll (event) for a user.
     * @param pollId ID of the finished poll (event).
     * @param userId ID of the user.
     */
    public static void addEventForUser(String pollId, String userId) {
        ref.child(userId).child("polls").child(pollId).setValue(true);
    }

    /**
     * Removes a finished poll (event) for a user.
     * @param pollId ID of the finished poll (event).
     * @param userId ID of the user.
     */
    public static void removeEventForUser(String pollId, String userId) {
        ref.child(userId).child("polls").child(pollId).setValue(null);
    }
}
