package io.github.entertainmatch.firebase;

import android.util.Log;

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

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;

import javax.inject.Inject;

/**
 * Created by Adrian Bednarz on 4/30/17.
 *
 * The controller used to retrieve and vote information about polls in Firebase.
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
     * Adds poll information to the database.
     * @param newPoll Newly created poll.
     */
    public static Poll addPoll(String facebookHostId, PollStub newPoll) {
        DatabaseReference firebasePollRef = ref.push();
        FirebasePoll firebasePoll = FirebasePoll.fromPoll(facebookHostId, newPoll, firebasePollRef.getKey());
        firebasePoll.setFacebookUsers(null);

        // add poll to firebase
        firebasePollRef.setValue(firebasePoll);

        // delegate person controller to vote people
        FirebaseUserController.addPoll(
                firebasePollRef.getKey(),
                firebasePoll.getParticipants(),
                facebookHostId);

        // return poll id
        String pollId = firebasePollRef.getKey();
        return new Poll(
                newPoll.getName(),
                new VoteCategoryStage(pollId),
                newPoll.getMembers(),
                pollId,
                null
        );
    }

    /**
     * Returns a poll with the supplied string ID.
     * @param pollId ID string of the requested poll.
     * @return An {@link Observable} yielding {@link FirebasePoll} updates
     * after every update.
     */
    public static Observable<FirebasePoll> getPoll(String pollId) {
        return RxFirebaseDatabase.observeValueEvent(ref.child(pollId), FirebasePoll.class);
    }

    /**
     * Handles a single user's vote for a category.
     * @param pollId ID of the poll the user voted in.
     * @param facebookId Facebook ID of the user.
     * @param itemId ID of the category the user voted for.
     * @param city The city for which the poll was created.
     */
    public static void vote(String pollId, String facebookId, String itemId, String city) {
        ref.child(pollId).runTransaction(new CategoryVoteHandler(itemId, facebookId, pollId, city));
    }

    /**
     * Returns the winning category for a poll.
     * @param value An {@link Object} to be downcast to get a result.
     * @return The key with the most votes.
     */
    private static List<String> getWinningCategory(Object value) {
        HashMap<String, Long> voteCounts = (HashMap<String, Long>) value;
        return HashMapExt.getMax(voteCounts);
    }

    /**
     * Updates a user's remaining event choices after he's dismissed one
     * with a swipe.
     * @param pollId ID of the poll.
     * @param facebookId Facebook ID of the user.
     * @param selections Remaining selections of the user.
     */
    public static void updateRemainingEvents(String pollId, String facebookId, Map<String, Boolean> selections) {
        ArrayList<String> eventIds = new ArrayList<>();
        for (Map.Entry<String, Boolean> selection : selections.entrySet()) {
            if (selection.getValue()) {
                eventIds.add(selection.getKey());
            }
        }

        // maybe transaction, maybe not
        ref.child(pollId)
                .child("remainingEventChoices")
                .child(facebookId)
                .setValue(eventIds);
    }

    /**
     * Handles a single vote for an event.
     * @param pollId ID of the poll the user voted in.
     * @param facebookId Facebook ID of the user.
     * @param item The {@link Event} the user voted for.
     */
    public static void voteEvent(String pollId, String facebookId, Event item) {
        ref.child(pollId).runTransaction(new EventVoteHandler(facebookId, item, pollId));
    }

    /**
     * Gets poll information once.
     * @param pollId ID string of the poll.
     * @return An {@link Observable} yielding a single {@link FirebasePoll} object.
     */
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
     * @param pollId Current poll
     */
    private static void checkDateVotingMoveToNextStage(String pollId, String facebookId) {
        ref.child(pollId).child("eventDatesStatus").child("voted")
                .runTransaction(new DateVoteHandler(pollId, facebookId));
    }

    /**
     * Collects location information about chosen event
     * Used in date stage
     * @param pollId Poll to get information about
     * @return Observable with retrieved event dates. It provides data once in sense that it will incrementally return the same list.
     */
    public static Observable<List<EventDate>> getLocations(String pollId, String facebookId) {
        return FirebasePollController.getPollOnce(pollId).flatMap(poll ->
                FirebaseEventDateController.getEventDatesSingle(poll.getVictoriousEvent()).flatMap(eventDates -> {
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
        }));
    }

    /**
     * Removes again flag - used to indicate that event voting runs again
     * @param pollId Poll to remove flag in
     * @param facebookId User to remove flag for
     */
    public static void removeVoteEventAgainFlag(String pollId, String facebookId) {
        ref.child(pollId).child("again").child(facebookId).setValue(false);
    }

    private static class CategoryVoteHandler implements Transaction.Handler {
        private final String itemId;
        private final String facebookId;
        private final String pollId;
        private final String city;

        public CategoryVoteHandler(String itemId, String facebookId, String pollId, String city) {
            this.itemId = itemId;
            this.facebookId = facebookId;
            this.pollId = pollId;
            this.city = city;
        }

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
                    String imageUri = FirebaseCategoriesTemplatesController.getImageForCategory(winningCategory);
                    mutableData.child("drawableUri").setValue(imageUri);
                    FirebaseUserController.setupEventStage(pollId, votedFor.keySet());

                    FirebaseEventController.getEventsSingle(
                            city,
                            winningCategories.get(0)
                    ).subscribe(events -> {
                        ArrayList<String> eventIds = new ArrayList<>();
                        for (Event event : events.values()) {
                            eventIds.add(event.getId());
                        }
                        ref.child(pollId).child("eventsToVote").setValue(eventIds);
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
    }

    private static class EventVoteHandler implements Transaction.Handler {
        private final String facebookId;
        private final Event item;
        private final String pollId;

        public EventVoteHandler(String facebookId, Event item, String pollId) {
            this.facebookId = facebookId;
            this.item = item;
            this.pollId = pollId;
        }

        @Override
        public Transaction.Result doTransaction(MutableData mutableData) {
            MutableData eventVotesRef = mutableData.child("eventVotes");
            HashMap<String, String> eventVotes = (HashMap<String, String>) eventVotesRef.getValue();
            List<String> eventsToVote = (ArrayList<String>) mutableData.child("eventsToVote").getValue();

            eventVotes.put(facebookId, item.getId());
            eventVotesRef.child(facebookId).setValue(item.getId());
            if (HashMapExt.all(eventVotes, x -> !x.equals(FirebasePoll.NO_USER_VOTE))) {
                List<String> candidates = HashMapExt.mostFrequent(eventVotes);

                if (candidates.size() == 1 || eventsToVote.size() == 2) {
                    mutableData.child("stage").setValue(VoteDateStage.class.toString());
                    mutableData.child("victoriousEvent").setValue(item.getId());
                    mutableData.child("drawableUri").setValue(item.getDrawableUri());
                    FirebasePollController.getPollOnce(pollId).subscribe(poll -> {
                        FirebaseEventDateController.setupDataStage(poll, candidates.get(0));
                    });
                } else {
                    mutableData.child("eventsToVote").setValue(candidates);
                    mutableData.child("remainingEventChoices").setValue(null);
                    for (String facebookId : eventVotes.keySet()) {
                        eventVotes.put(facebookId, FirebasePoll.NO_USER_VOTE);
                        // vote again flag
                        mutableData.child("again").child(facebookId).setValue(true);
                    }
                    eventVotesRef.setValue(eventVotes);
                }

            }
            return Transaction.success(mutableData);
        }

        @Override
        public void onComplete(DatabaseError databaseError, boolean b, DataSnapshot dataSnapshot) {

        }
    }

    private static class DateVoteHandler implements Transaction.Handler {
        private final String pollId;
        private final String facebookId;

        public DateVoteHandler(String pollId, String facebookId) {
            this.pollId = pollId;
            this.facebookId = facebookId;
        }

        @Override
        public Transaction.Result doTransaction(MutableData mutableData) {
            for (MutableData facebookIdToVoted : mutableData.getChildren()) {
               if (facebookIdToVoted.getValue() == Boolean.FALSE) {
                   return Transaction.success(mutableData);
               }
            }

            ref.child(pollId).child("stage").setValue(VoteResultStage.class.toString());

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
                String locationId = HashMapExt.getMax(locationToCounts).get(0);
                ref.child(pollId).child("chosenLocationId").setValue(locationId);
                FirebaseCompletedPollController.pollCompleted(poll, locationId);
                FirebaseUserController.setupResultStage(pollId, facebookId);
            });

            return Transaction.success(mutableData);
        }

        @Override
        public void onComplete(DatabaseError databaseError, boolean b, DataSnapshot dataSnapshot) {

        }
    }
}
