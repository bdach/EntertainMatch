package io.github.entertainmatch.firebase;

import android.util.Log;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.kelvinapps.rxfirebase.RxFirebaseDatabase;

import java.util.List;

import io.github.entertainmatch.firebase.models.FirebasePerson;
import io.github.entertainmatch.firebase.models.FirebasePoll;
import io.github.entertainmatch.model.Person;
import io.github.entertainmatch.model.Poll;
import io.github.entertainmatch.utils.ListExt;
import rx.Observable;

/**
 * Created by Adrian Bednarz on 4/30/17.
 */

public class FirebasePersonController {
    /**
     * Instance of the database
     */
    private static final FirebaseDatabase database = FirebaseDatabase.getInstance();
    /**
     * Holds reference to people collection.
     * In this collection each node is denoted by user's facebook id.
     */
    private static final DatabaseReference ref = database.getReference("people");

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
     * @param firebasePerson User to get poll information for
     * @return Observables of polls for given user
     */
    public static List<Observable<FirebasePoll>> getPollsForUser(FirebasePerson firebasePerson) {
        return ListExt.map(firebasePerson.getPollIds(),
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
            ref.child(facebookId).child("polls").child(pollId).setValue("");
        }
    }
}
