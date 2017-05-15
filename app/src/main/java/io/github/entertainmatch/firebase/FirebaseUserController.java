package io.github.entertainmatch.firebase;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.kelvinapps.rxfirebase.RxFirebaseDatabase;

import java.util.ArrayList;
import java.util.List;

import io.github.entertainmatch.firebase.models.FirebaseUser;
import io.github.entertainmatch.firebase.models.FirebasePoll;
import io.github.entertainmatch.model.Person;
import io.github.entertainmatch.utils.ListExt;
import rx.Observable;

/**
 * Created by Adrian Bednarz on 4/30/17.
 *
 * Manages user state stored in firebase
 */
public class FirebaseUserController {
    /**
     * Instance of the database
     */
    private static final FirebaseDatabase database = FirebaseDatabase.getInstance();
    /**
     * Holds reference to people collection.
     * In this collection each node is denoted by user's facebook id.
     */
    private static final DatabaseReference ref = database.getReference("user_polls");

    /**
     * Adds person information to the database
     * @param person Freshly authorized person
     */
    public static void addPerson(Person person) {
        ref.child(person.getFacebookId());
    }

    /**
     * Retrieves observables for all polls of the user.
     * TODO: Not too handy to use probably
     * @param firebaseUser User to get poll information for
     * @return Observables of polls for given user
     */
    public static List<Observable<FirebasePoll>> getPollsForUser(FirebaseUser firebaseUser) {
        return ListExt.map(new ArrayList<>(firebaseUser.getPolls().keySet()),
                pollId -> RxFirebaseDatabase.observeValueEvent(ref.child(pollId),
                        FirebasePoll.class));
    }

    /**
     * Adds poll for a user
     * @param pollId Id of new poll to add for all the users.
     * @param membersFacebookIds Facebook ids of members
     */
    public static void addPoll(String pollId, List<String> membersFacebookIds) {
        for (String facebookId : membersFacebookIds) {
            ref.child(facebookId).child("polls").child(pollId).setValue(true);
        }
    }

    public static void removePoll(String pollId, String userId) {
        ref.child(userId).child("polls").child(pollId).setValue(null);
    }

    /**
     * Grabs user information stored in firebase by facebook id.
     * Initially used to fetch data about polls.
     * @param facebookId User's facebook id.
     * @return Observable to the person provided by Firebase
     */
    public static Observable<FirebaseUser> getUserOnce(String facebookId) {
        return RxFirebaseDatabase.observeSingleValueEvent(
                ref.child(facebookId),
                FirebaseUser.class);
    }
}
