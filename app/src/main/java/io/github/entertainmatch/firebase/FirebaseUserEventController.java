package io.github.entertainmatch.firebase;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.kelvinapps.rxfirebase.RxFirebaseDatabase;
import io.github.entertainmatch.firebase.models.FirebasePoll;
import io.github.entertainmatch.firebase.models.FirebaseUser;
import io.github.entertainmatch.model.Person;
import rx.Observable;


/**
 * @author Bartlomiej Dach
 * @since 16.05.17
 */
public class FirebaseUserEventController {
    private static final FirebaseDatabase database = FirebaseDatabase.getInstance();
    private static final DatabaseReference ref = database.getReference("user_events");

    public static void addPerson(Person person) {
        ref.child(person.getFacebookId());
    }

    public static Observable<FirebasePoll> getEventsForUser(String facebookId) {
        return RxFirebaseDatabase.observeValueEvent(ref.child(facebookId), FirebaseUser.class)
                .flatMap(user -> Observable.merge(FirebasePollController.getPollsForUser(user)));
    }

    public static void addEventForUser(String pollId, String userId) {
        ref.child(userId).child("polls").child(pollId).setValue(true);
    }
}
