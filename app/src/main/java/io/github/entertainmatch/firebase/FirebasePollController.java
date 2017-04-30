package io.github.entertainmatch.firebase;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.kelvinapps.rxfirebase.RxFirebaseDatabase;

import java.util.ArrayList;
import java.util.List;

import io.github.entertainmatch.firebase.models.FirebasePerson;
import io.github.entertainmatch.firebase.models.FirebasePoll;
import io.github.entertainmatch.model.Poll;
import io.github.entertainmatch.utils.ListExt;
import rx.Observable;

/**
 * Created by Adrian Bednarz on 4/30/17.
 *
 * The controller used to retrieve and update information about polls in the Firebase.
 */
public class FirebasePollController {
    /**
     * Instance of the database
     */
    private static final FirebaseDatabase database = FirebaseDatabase.getInstance();
    /**
     * Holds reference to polls collection.
     * In this collection each node is denoted by a poll id.
     * User can retrieve poll ids with <code>FirebaseUserController.getUserById(facebookId)</code>
     */
    private static final DatabaseReference ref = database.getReference("polls");

    /**
     * Adds poll information to the database
     * @param newPoll Newly created poll
     */
    public static void addPoll(String facebookHostId, Poll newPoll) {
        DatabaseReference firebasePollRef = ref.push();
        FirebasePoll firebasePoll = FirebasePoll.fromPoll(facebookHostId, newPoll);

        // add poll to firebase
        firebasePollRef.setValue(firebasePoll);

        // delegate person controller to update people
        FirebasePersonController.addPoll(
                firebasePollRef.getKey(),
                firebasePoll.getMemberFacebookIds());
    }

    /**
     * Retrieves observables for all polls of the user.
     * TODO: Not too handy to use probably
     * @param firebasePerson User to get poll information for
     * @return Observables of polls for given user
     */
    public static List<Observable<FirebasePoll>> getPollsForUser(FirebasePerson firebasePerson) {
        return ListExt.map(new ArrayList<>(firebasePerson.getPolls().keySet()),
                pollId -> RxFirebaseDatabase.observeValueEvent(ref.child(pollId),
                    FirebasePoll.class));
    }
}
