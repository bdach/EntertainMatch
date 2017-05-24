package io.github.entertainmatch.firebase;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.MutableData;
import com.google.firebase.database.Transaction;
import com.kelvinapps.rxfirebase.RxFirebaseDatabase;
import io.github.entertainmatch.facebook.FacebookUsers;
import io.github.entertainmatch.firebase.models.FirebaseEventDate;
import io.github.entertainmatch.firebase.models.FirebasePoll;
import io.github.entertainmatch.firebase.models.FirebaseUser;
import io.github.entertainmatch.model.Category;
import io.github.entertainmatch.model.Event;
import io.github.entertainmatch.model.EventDate;
import io.github.entertainmatch.model.Poll;
import io.github.entertainmatch.model.PollStub;
import io.github.entertainmatch.model.VoteCategoryStage;
import io.github.entertainmatch.model.VoteDateStage;
import io.github.entertainmatch.model.VoteEventStage;
import io.github.entertainmatch.model.VoteResultStage;
import io.github.entertainmatch.utils.HashMapExt;
import io.github.entertainmatch.utils.ListExt;
import rx.Observable;
import rx.subjects.PublishSubject;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;

/**
 * Created by Adrian Bednarz on 4/30/17.
 *
 * The controller used to retrieve and vote information about polls in the Firebase.
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

    /**
     * Retrieves observables for all polls of the user.
     * @param firebaseUser User to get poll information for
     * @return Observables of polls for given user
     */
    public static List<Observable<FirebasePoll>> getPollsForUser(FirebaseUser firebaseUser) {
        return firebaseUser == null ? new ArrayList<>() :
                ListExt.map(new ArrayList<>(firebaseUser.getPolls().keySet()),
                    pollId -> RxFirebaseDatabase.observeValueEvent(ref.child(pollId),
                        FirebasePoll.class));
    }

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
                firebasePoll.getParticipants(),
                facebookHostId);

        // return poll id
        String pollId = firebasePollRef.getKey();
        return new Poll(newPoll.getName(), new VoteCategoryStage(pollId), newPoll.getMembers(), pollId, null);
    }

    public static Observable<FirebasePoll> getPoll(String pollId) {
        return RxFirebaseDatabase.observeValueEvent(ref.child(pollId), FirebasePoll.class);
    }

    public static void vote(String pollId, String facebookId, String itemId) {
        ref.child(pollId).runTransaction(new Transaction.Handler() {
            @Override
            public Transaction.Result doTransaction(MutableData mutableData) {
                MutableData voteCountsRef = mutableData.child("voteCounts");
                HashMap<String, Long> voteCountsMap = (HashMap<String, Long>)voteCountsRef.getValue();
                voteCountsRef.child(itemId).setValue(voteCountsRef.child(itemId).getValue(Long.class) + 1);

                MutableData votedForRef = mutableData.child("votedFor");
                HashMap<String, String> votedFor = (HashMap<String, String>) votedForRef.getValue();
                votedFor.put(facebookId, itemId);
                votedForRef.setValue(votedFor);

                // check next stage
                if (HashMapExt.all(votedFor, x -> !x.equals(FirebasePoll.NO_USER_VOTE))) {
                    List<String> winningCategories = getWinningCategory(voteCountsRef.getValue());
                    // reduce problem size
                    if (winningCategories.size() == voteCountsMap.size()) {
                        winningCategories.remove(new Random().nextInt(winningCategories.size()));
                    }

                    if (winningCategories.size() == 1) {
                        mutableData.child("stage").setValue(VoteEventStage.class.toString());
                        String winningCategory = winningCategories.get(0);
                        mutableData.child("chosenCategory").setValue(winningCategory);
                        String imageUri = FirebaseCategoriesTemplatesController.getDrawableForCategory(winningCategory);
                        mutableData.child("drawableUri").setValue(imageUri);
                        FirebaseUserController.setupEventStage(pollId, votedFor.keySet(), facebookId);

                        FirebaseEventController.getEventsSingle(winningCategories.get(0)).subscribe(events -> {
                            ref.child(pollId).child("eventsToVote").setValue(ListExt.map(events, Event::getId));
                        });

                    } else {
                        for (Category category : FirebaseCategoriesTemplatesController.getCached()) {
                            if (!winningCategories.contains(category.getId())) {
                                mutableData.child("voteCounts").child(category.getId()).setValue(null);
                            } else {
                                mutableData.child("voteCounts").child(category.getId()).setValue(0);
                            }
                        }
                        for (MutableData votedForReset : mutableData.child("votedFor").getChildren()) {
                            votedForReset.setValue(FirebasePoll.NO_USER_VOTE);
                        }
                    }
                }

                return Transaction.success(mutableData);
            }

            @Override
            public void onComplete(DatabaseError databaseError, boolean b, DataSnapshot dataSnapshot) {

            }
        });
    }

    private static List<String> getWinningCategory(Object value) {
        HashMap<String, Long> voteCounts = (HashMap<String, Long>) value;
        return HashMapExt.getMax(voteCounts);
    }

    public static void updateRemainingEvents(String pollId, String facebookId, Map<String, Boolean> selections) {
        ref.child(pollId)
                .child("remainingEventChoices")
                .child(facebookId)
                .setValue(selections);
    }

    public static void voteEvent(String pollId, String facebookId, Event item) {
        ref.child(pollId).runTransaction(new Transaction.Handler() {
            @Override
            public Transaction.Result doTransaction(MutableData mutableData) {
                MutableData eventVotesRef = mutableData.child("eventVotes");
                HashMap<String, String> eventVotes = (HashMap<String, String>) eventVotesRef.getValue();
                eventVotes.put(facebookId, item.getId());
                eventVotesRef.setValue(eventVotes);
                if (HashMapExt.all(eventVotes, x -> !x.equals(FirebasePoll.NO_USER_VOTE))) {
                    List<String> candidates = HashMapExt.mostFrequent(eventVotes);

                    if (candidates.size() == 1) {
                        mutableData.child("stage").setValue(VoteDateStage.class.toString());
                        mutableData.child("victoriousEvent").setValue(item.getId());
                        mutableData.child("drawableUri").setValue(item.getDrawableUri());
                        // TODO: not sure
                        FirebasePollController.getPollOnce(pollId).subscribe(poll -> {
                            FirebaseEventDateController.setupDataStage(poll, candidates.get(0));
                        });
                    } else {
                        mutableData.child("eventsToVote").setValue(candidates);
                        mutableData.child("remainingEventChoices").setValue(null);
                        for (String key : eventVotes.keySet()) {
                            eventVotes.put(key, FirebasePoll.NO_USER_VOTE);
                        }
                        eventVotesRef.setValue(eventVotes);
                    }

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

        FirebaseUserController.setupDateStage(pollId, participantId);
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

        checkDateVotingMoveToNextStage(pollId, facebookId);
    }

    /**
     * Checks whether its time to move to next stage.
     * TODO: not sure if there won't be any races between users
     * @param pollId Current poll
     */
    private static void checkDateVotingMoveToNextStage(String pollId, String facebookId) {
        ref.child(pollId).child("eventDatesStatus").child("voted").runTransaction(new Transaction.Handler() {
            @Override
            public Transaction.Result doTransaction(MutableData mutableData) {
                for (MutableData facebookIdToVoted : mutableData.getChildren()) {
                   if (facebookIdToVoted.getValue() == Boolean.FALSE) {
                       return Transaction.success(mutableData);
                   }
                }

                ref.child(pollId).child("stage").setValue(VoteResultStage.class.toString());

                // TODO: not sure
                FirebasePollController.getPollOnce(pollId).subscribe(poll -> {
                    HashMap<String, Long> locationToCounts = new HashMap<>();
                    HashMapExt.forEach(poll.getEventDatesStatus(), (locationId, facebookIdToChosen) -> {
                        if (locationId.equals("voted"))
                            return;

                        long votes = 0L;
                        for (Boolean chosen : facebookIdToChosen.values()) {
                            votes += chosen ? 1 : 0;
                        }

                        locationToCounts.put(locationId, votes);
                    });
                    ref.child(pollId).child("chosenLocationId").setValue(HashMapExt.getMax(locationToCounts).get(0));
                    FirebaseCompletedPollController.pollCompleted(poll);
                    FirebaseUserController.setupResultStage(pollId, facebookId);
                });

                return Transaction.success(mutableData);
            }

            @Override
            public void onComplete(DatabaseError databaseError, boolean b, DataSnapshot dataSnapshot) {

            }
        });
    }

    /**
     * Collects location information about chosen event
     * Used in date stage
     * @param pollId Poll to get information about
     * @return Observable with retrieved event dates. It provides data once in sense that it will incrementally return the same list.
     */
    public static Observable<List<EventDate>> getLocations(String pollId) {
        return FirebasePollController.getPollOnce(pollId).flatMap(poll -> {

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
        });
    }
}
