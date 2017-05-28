package io.github.entertainmatch.firebase;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.MutableData;
import com.google.firebase.database.Transaction;
import com.kelvinapps.rxfirebase.RxFirebaseDatabase;
import io.github.entertainmatch.firebase.models.FirebasePoll;
import io.github.entertainmatch.firebase.models.FirebaseUser;
import io.github.entertainmatch.model.Person;
import rx.Observable;

import java.util.List;
import java.util.Set;

/**
 * Manages user state stored in Firebase.
 *
 * @author Adrian Bednarz
 * @since 4/30/17
 */
public class FirebaseUserController {
    /**
     * Instance of the database.
     */
    private static final FirebaseDatabase database = FirebaseDatabase.getInstance();

    /**
     * Holds reference to people collection.
     * In this collection each node is denoted by user's facebook id.
     */
    private static final DatabaseReference ref = database.getReference("user_polls");

    /**
     * Adds person information to the database.
     * @param person Freshly authorized person.
     */
    public static void addPerson(Person person) {
        ref.child(person.getFacebookId());
    }

    /**
     * Adds poll for a user.
     * @param pollId Id of new poll to add for all the users.
     * @param membersFacebookIds Facebook ids of members.
     * @param creatorUserId Poll creator facebookId.
     */
    public static void addPoll(String pollId, List<String> membersFacebookIds, String creatorUserId) {
        for (String facebookId : membersFacebookIds) {
            ref.child(facebookId).child("polls").child(pollId).setValue(!facebookId.equals(creatorUserId));
        }
    }

    /**
     * Remove a poll from the user's list of active poll.
     * @param pollId ID of poll to clear.
     * @param userId ID of user for whom the poll should be removed.
     */
    public static void removePollForUser(String pollId, String userId) {
        ref.child(userId).child("polls").child(pollId).setValue(null);
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
        ref.child(facebookId).runTransaction(new NotificationResetHandler(user));
    }

    /**
     * Fires a notification for all users in the given poll upon moving to
     * {@link io.github.entertainmatch.model.VoteEventStage}
     * by setting the proper Firebase values.
     * @param pollId ID of the poll to fire a notification for.
     * @param facebookIds A {@link Set} containing IDs of all of the poll's participants.
     */
    public static void setupEventStage(String pollId, Set<String> facebookIds) {
        for (String facebookId : facebookIds) {
            ref.child(facebookId).child("events").child(pollId).setValue(true);
        }
    }

    /**
     * Fires a notification for a single user in the given poll upon moving to
     * {@link io.github.entertainmatch.model.VoteDateStage}
     * by setting the proper Firebase values.
     * @param pollId ID of the poll to fire a notification for.
     * @param facebookId ID of the user to fire a notification for.
     */
    public static void setupDateStage(String pollId, String facebookId) {
        ref.child(facebookId).child("dates").child(pollId).setValue(true);
    }

    /**
     * Fires a notification for a single user in the given poll upon moving to
     * {@link io.github.entertainmatch.model.VoteResultStage}
     * by setting the proper Firebase values.
     * @param pollId ID of the poll to fire a notification for.
     * @param facebookId ID of the user to fire a notification for.
     */
    public static void setupResultStage(String pollId, String facebookId) {
        ref.child(facebookId).child("finished").child(pollId).setValue(true);
    }

    static class NotificationResetHandler implements Transaction.Handler {
        private final FirebaseUser user;

        public NotificationResetHandler(FirebaseUser user) {
            this.user = user;
        }

        @Override
        public Transaction.Result doTransaction(MutableData mutableData) {
            for (String pollId : user.getPolls().keySet()) {
                mutableData.child("polls").child(pollId).setValue(false);
            }

            for (String pollId : user.getEvents().keySet()) {
                mutableData.child("events").child(pollId).setValue(false);
            }

            for (String pollId : user.getDates().keySet()) {
                mutableData.child("dates").child(pollId).setValue(false);
            }

            for (String pollId : user.getFinished().keySet()) {
                mutableData.child("finished").child(pollId).setValue(false);
            }

            return Transaction.success(mutableData);
        }

        @Override
        public void onComplete(DatabaseError databaseError, boolean b, DataSnapshot dataSnapshot) {

        }
    }
}
