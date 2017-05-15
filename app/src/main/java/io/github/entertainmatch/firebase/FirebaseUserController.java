package io.github.entertainmatch.firebase;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.MutableData;
import com.google.firebase.database.Transaction;
import com.kelvinapps.rxfirebase.RxFirebaseDatabase;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

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
     * Adds poll for a user
     * @param pollId Id of new poll to add for all the users.
     * @param membersFacebookIds Facebook ids of members
     * @param creatorUserId Poll creator facebookId
     */
    public static void addPoll(String pollId, List<String> membersFacebookIds, String creatorUserId) {
        for (String facebookId : membersFacebookIds) {
            ref.child(facebookId).child("polls").child(pollId).setValue(!facebookId.equals(creatorUserId));
        }
    }

    public static void removePoll(String pollId, String userId) {
        ref.child(userId).child("polls").child(pollId).setValue(null);
    }

    /**
     * Grabs user information stored in firebase by facebook id.
     * Initially used to fetch data about polls.
     * @param facebookId User's facebook id.
     * @return Observable to the person provided by Firebase which fires one time only
     */
    public static Observable<FirebaseUser> getUserOnce(String facebookId) {
        return RxFirebaseDatabase.observeSingleValueEvent(
                ref.child(facebookId),
                FirebaseUser.class);
    }

    /**
     * Observes any poll in which user takes part
     * @param facebookId User's facebook ID
     * @return Observable Observable of polls with given user
     */
    public static Observable<FirebasePoll> getPollsForUser(String facebookId) {
        return RxFirebaseDatabase.observeValueEvent(ref.child(facebookId), FirebaseUser.class)
            .flatMap(user -> Observable.merge(FirebasePollController.getPollsForUser(user)));
    }

    /**
     * Grabs user information stored in firebase by facebook id.
     * @param facebookId User's facebook id.
     * @return Observable to the person provided by Firebase
     */
    public static Observable<FirebaseUser> getUser(String facebookId) {
        return RxFirebaseDatabase.observeValueEvent(
                ref.child(facebookId),
                FirebaseUser.class);
    }

    /**
     * Resets all user poll's flags to false indicating that user has already been informed
     * about this poll
     * @param facebookId Facebook id of user to consider
     * @param user Firebase user representation to change. Should be paired with provided facebookId
     */
    public static void makePollsOldForUser(String facebookId, FirebaseUser user) {
        ref.child(facebookId).child("polls").runTransaction(new Transaction.Handler() {
            @Override
            public Transaction.Result doTransaction(MutableData mutableData) {
                for (String pollId : user.getPolls().keySet()) {
                    mutableData.child(pollId).setValue(false);
                }
                return Transaction.success(mutableData);
            }

            @Override
            public void onComplete(DatabaseError databaseError, boolean b, DataSnapshot dataSnapshot) {

            }
        });
    }
}
