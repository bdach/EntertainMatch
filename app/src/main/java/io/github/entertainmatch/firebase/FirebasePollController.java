package io.github.entertainmatch.firebase;

import android.content.Intent;
import android.hardware.Camera;
import android.util.Log;

import com.google.firebase.FirebaseOptions;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.MutableData;
import com.google.firebase.database.Transaction;
import com.kelvinapps.rxfirebase.RxFirebaseDatabase;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import io.github.entertainmatch.facebook.FacebookUsers;
import io.github.entertainmatch.firebase.models.FirebaseCategory;
import io.github.entertainmatch.firebase.models.FirebaseEventDate;
import io.github.entertainmatch.firebase.models.FirebaseLocation;
import io.github.entertainmatch.firebase.models.FirebaseUser;
import io.github.entertainmatch.firebase.models.FirebasePoll;
import io.github.entertainmatch.model.*;
import io.github.entertainmatch.utils.HashMapExt;
import io.github.entertainmatch.utils.ListExt;
import rx.Observable;
import rx.subjects.PublishSubject;
import rx.subjects.Subject;

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
                    mutableData.child("chosenCategory").setValue(getWinningCategory(voteCountsRef.getValue()));
                }

                return Transaction.success(mutableData);
            }

            @Override
            public void onComplete(DatabaseError databaseError, boolean b, DataSnapshot dataSnapshot) {

            }
        });
    }

    private static String getWinningCategory(Object value) {
        HashMap<String, Long> voteCounts = (HashMap<String, Long>) value;
        // TODO ties
        return HashMapExt.getMax(voteCounts);
    }

    public static void updateRemainingEvents(String pollId, String facebookId, Map<String, Boolean> selections) {
        ref.child(pollId)
                .child("remainingEventChoices")
                .child(facebookId)
                .setValue(selections);
    }

    public static void voteEvent(String pollId, String facebookId, String itemId) {
        ref.child(pollId).runTransaction(new Transaction.Handler() {
            @Override
            public Transaction.Result doTransaction(MutableData mutableData) {
                MutableData eventVotesRef = mutableData.child("eventVotes");
                HashMap<String, String> eventVotes = (HashMap<String, String>) eventVotesRef.getValue();
                eventVotes.put(facebookId, itemId);
                eventVotesRef.setValue(eventVotes);
                if (HashMapExt.all(eventVotes, x -> !x.equals(FirebasePoll.NO_USER_VOTE))) {
                    mutableData.child("stage").setValue(VoteDateStage.class.toString());

                    mutableData.child("victoriousEvent").setValue(HashMapExt.mostFrequent(eventVotes));
                    FirebaseEventDateController.setup(FirebasePollController.polls.get(pollId), HashMapExt.mostFrequent(eventVotes));
                }
                return Transaction.success(mutableData);
            }

            @Override
            public void onComplete(DatabaseError databaseError, boolean b, DataSnapshot dataSnapshot) {

            }
        });
    }

    public static Observable<FirebasePoll> getPollOnce(String pollId) {
        return RxFirebaseDatabase.observeSingleValueEvent(ref.child(pollId), FirebasePoll.class);
    }

    /**
     * Initializes location flags for user in poll object in firebase
     * @param pollId Current poll
     * @param locationId Current location
     * @param participantId User id to set values for
     */
    public static void setupDateStageForUser(String pollId, String locationId, String participantId) {
        DatabaseReference eventDatesRef = ref.child(pollId).child("eventDatesStatus");

        eventDatesRef.child(locationId).child(participantId).setValue(false);
        eventDatesRef.child("voted").child(participantId).setValue(false);
    }

    /**
     * Registers user attitude to take part in an event at the given time and place
     * @param pollId Current poll
     * @param locationId Current location
     * @param facebookId Current user id
     * @param selection Whether user wants to go at given date
     */
    public static void chooseDate(String pollId, String locationId, String facebookId, Boolean selection) {
        DatabaseReference eventDatesRef = ref.child(pollId).child("eventDatesStatus");

        eventDatesRef.child(locationId)
            .child(facebookId)
            .setValue(selection);
    }

    /**
     * Notify database that user has finished voting
     * @param pollId Current poll id
     * @param facebookId Current user id
     */
    public static void dateVotingFinished(String pollId, String facebookId) {
        ref.child(pollId).child("eventDatesStatus")
            .child("voted")
            .child(facebookId)
            .setValue(true);
    }

    /**
     * Collects location information about chosen event
     * Used in date stage
     * @param pollId Poll to get information about
     * @return Observable with retrieved event dates. It provides data once in sense that it will incrementally return the same list.
     */
    public static Observable<List<EventDate>> getLocations(String pollId) {
        FirebasePoll poll = FirebasePollController.polls.get(pollId);
        String facebookId = FacebookUsers.getCurrentUser(null).getFacebookId();

        return FirebaseEventDateController.getEventDatesSingle(poll.getChosenCategory(), poll.getVictoriousEvent()).flatMap(eventDates -> {
            List<EventDate> results = new ArrayList<>();
            PublishSubject<List<EventDate>> observable = PublishSubject.create();

            for (FirebaseEventDate eventDate : eventDates.values()) {
                FirebaseLocationsController.getLocationOnce(eventDate.getLocationId()).subscribe(location -> {
                    results.add(new EventDate(
                        eventDate.getEventId(),
                        location.getId(),
                        location.getPlace(),
                        location.getLat(),
                        location.getLat(),
                        new Date(eventDate.getDate()),
                        poll.getEventDatesStatus()
                                .get(location.getId())
                                .get(facebookId)));

                    observable.onNext(results);
                });
            }

            return observable;
        });
    }
}
