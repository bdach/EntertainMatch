package io.github.entertainmatch.firebase;

import android.content.Intent;
import android.util.Log;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.MutableData;
import com.google.firebase.database.Transaction;
import com.kelvinapps.rxfirebase.RxFirebaseDatabase;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import io.github.entertainmatch.firebase.models.FirebaseCategory;
import io.github.entertainmatch.firebase.models.FirebaseUser;
import io.github.entertainmatch.firebase.models.FirebasePoll;
import io.github.entertainmatch.model.Poll;
import io.github.entertainmatch.model.PollStub;
import io.github.entertainmatch.model.VoteCategoryStage;
import io.github.entertainmatch.model.VoteEventStage;
import io.github.entertainmatch.utils.HashMapExt;
import io.github.entertainmatch.utils.ListExt;
import rx.Observable;

/**
 * Created by Adrian Bednarz on 4/30/17.
 *
 * The controller used to retrieve and vote information about polls in the Firebase.
 */
public class FirebasePollController {
    public static Map<String, FirebasePoll> polls = new HashMap<>();
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
    public static Poll addPoll(String facebookHostId, PollStub newPoll) {
        DatabaseReference firebasePollRef = ref.push();
        FirebasePoll firebasePoll = FirebasePoll.fromPoll(facebookHostId, newPoll, firebasePollRef.getKey());

        // add poll to firebase
        firebasePollRef.setValue(firebasePoll);

        // delegate person controller to vote people
        FirebaseUserController.addPoll(
                firebasePollRef.getKey(),
                firebasePoll.getParticipants());

        // return poll id
        String pollId = firebasePollRef.getKey();
        return new Poll(newPoll.getName(), new VoteCategoryStage(pollId), newPoll.getMembers(), pollId);
    }

    /**
     * Retrieves observables for all polls of the user.
     * This is a one-use-only observable. Used primarily to add polls to the view.
     * One should resubscribe with <code>getPoll</code> observable for further changes.
     * @param firebaseUser User to get poll information for
     * @return Observables of polls for given user
     */
    public static List<Observable<FirebasePoll>> getPollsOnceForUser(FirebaseUser firebaseUser) {
        return ListExt.map(new ArrayList<>(firebaseUser.getPolls().keySet()),
                pollId -> RxFirebaseDatabase.observeSingleValueEvent(ref.child(pollId),
                    FirebasePoll.class));
    }

    public static Observable<FirebasePoll> getPoll(String pollId) {
        return RxFirebaseDatabase.observeValueEvent(ref.child(pollId), FirebasePoll.class);
    }

    public static void vote(String pollId, String facebookId, String itemId) {
        ref.child(pollId).runTransaction(new Transaction.Handler() {
            @Override
            public Transaction.Result doTransaction(MutableData mutableData) {
                MutableData voteCountsRef = mutableData.child("voteCounts");
                voteCountsRef.child(itemId).setValue(voteCountsRef.child(itemId).getValue(Long.class) + 1);

                MutableData votedForRef = mutableData.child("votedFor");
                HashMap<String, String> votedFor = (HashMap<String, String>) votedForRef.getValue();
                votedFor.put(facebookId, itemId);
                votedForRef.setValue(votedFor);

                // check next stage
                if (HashMapExt.all(votedFor, x -> !x.equals(FirebasePoll.NO_USER_VOTE))) {
                    mutableData.child("stage").setValue(VoteEventStage.class.toString());
                }

                return Transaction.success(mutableData);
            }

            @Override
            public void onComplete(DatabaseError databaseError, boolean b, DataSnapshot dataSnapshot) {

            }
        });
    }

    public static void updateRemainingEvents(String pollId, String facebookId, Map<String, Boolean> selections) {
        ref.child(pollId)
                .child("remainingChoices")
                .child(facebookId)
                .setValue(selections);
    }

    public static Observable<FirebasePoll> getPollOnce(String pollId) {
        return RxFirebaseDatabase.observeSingleValueEvent(ref.child(pollId), FirebasePoll.class);
    }
}
