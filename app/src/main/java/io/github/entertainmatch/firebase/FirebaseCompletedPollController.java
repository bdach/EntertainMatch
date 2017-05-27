package io.github.entertainmatch.firebase;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.kelvinapps.rxfirebase.DataSnapshotMapper;
import com.kelvinapps.rxfirebase.RxFirebaseDatabase;
import io.github.entertainmatch.firebase.models.FirebaseCompletedPoll;
import io.github.entertainmatch.firebase.models.FirebaseEventDate;
import io.github.entertainmatch.firebase.models.FirebaseLocation;
import io.github.entertainmatch.firebase.models.FirebasePoll;
import io.github.entertainmatch.firebase.models.FirebaseUser;
import io.github.entertainmatch.model.Event;
import io.github.entertainmatch.utils.ListExt;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import rx.Observable;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Controller responsible for moving polls to the "completed"
 * node after the poll result is determined.
 *
 * @author Bartlomiej Dach
 * @since 23.05.17
 */
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class FirebaseCompletedPollController {
    /**
     * Instance of the database used.
     */
    private static final FirebaseDatabase database = FirebaseDatabase.getInstance();

    /**
     * Database reference used to store completed poll data.
     */
    private static final DatabaseReference ref = database.getReference("completed");

    /**
     * Adds a poll whose data is stored in a {@link FirebasePoll} to the
     * "completed" node, after fetching event and date information from
     * other controllers.
     * @param poll {@link FirebasePoll} object representing the finished poll.
     * @param locationId The ID string of the location chosen by the users.
     */
    public static void pollCompleted(FirebasePoll poll, String locationId) {
        Observable<? extends Event> eventSingle = FirebaseEventController.getEventSingle(
                poll.getChosenCategory(),
                poll.getVictoriousEvent()
        );
        Observable<FirebaseEventDate> eventDateSingle = FirebaseEventDateController.getEventSingle(
                poll.getVictoriousEvent(),
                locationId
        );
        Observable<FirebaseLocation> locationSingle = FirebaseLocationsController.getLocationOnce(locationId);
        saveCompletedPoll(poll, eventSingle, eventDateSingle, locationSingle);
    }

    /**
     * Zips three observables, containing event, event date and event location data,
     * and stores them in the database under the proper key.
     * @param poll {@link FirebasePoll} object representing the poll.
     * @param eventSingle An {@link Observable} yielding {@link Event} objects.
     * @param eventDateSingle An {@link Observable} yielding {@link FirebaseEventDate} objects.
     * @param locationSingle An {@link Observable} yielding {@link FirebaseLocation} objects.
     */
    static void saveCompletedPoll(FirebasePoll poll,
                                  Observable<? extends Event> eventSingle,
                                  Observable<FirebaseEventDate> eventDateSingle,
                                  Observable<FirebaseLocation> locationSingle) {
        Observable.zip(eventSingle, eventDateSingle, locationSingle,
                (event, eventDate, location) -> {
                    Map<String, Object> node = new HashMap<>();
                    node.put("id", poll.getPollId());
                    node.put("category", poll.getChosenCategory());
                    node.put("participants", poll.getParticipants());
                    node.put("event", event);
                    node.put("eventDate", eventDate);
                    node.put("location", location);
                    return node;
                }
        ).subscribe(node -> ref.child(poll.getPollId()).setValue(node));
    }

    /**
     * Returns an observable that will fetch a {@link FirebaseCompletedPoll} object a single time.
     * @param pollId ID string of the poll that is to be fetched.
     * @return An {@link Observable} yielding a {@link FirebaseCompletedPoll}, containing
     * the relevant data.
     */
    public static Observable<FirebaseCompletedPoll> getCompletedPollOnce(String pollId) {
        return RxFirebaseDatabase.observeSingleValueEvent(ref.child(pollId), FirebaseCompletedPollController::map);
    }

    /**
     * Returns a list of {@link Observable}s containing poll information for the given user.
     * @param user An instance of {@link FirebaseUser} representing the request sender.
     * @return A {@link List} of {@link Observable}s which yield {@link FirebaseCompletedPoll}
     * object.
     */
    public static List<Observable<FirebaseCompletedPoll>> getPollsForUser(FirebaseUser user) {
        if (user == null) {
            return Collections.emptyList();
        }
        ArrayList<String> pollIds = new ArrayList<>(user.getPolls().keySet());
        return ListExt.map(pollIds, pollId ->
                RxFirebaseDatabase.observeValueEvent(ref.child(pollId), FirebaseCompletedPollController::map));
    }

    /**
     * Maps a {@link DataSnapshot} containing completed poll information to a
     * {@link FirebaseCompletedPoll} object.
     * @param dataSnapshot The {@link DataSnapshot} to map.
     * @return A converted {@link FirebaseCompletedPoll} object.
     */
    public static FirebaseCompletedPoll map(DataSnapshot dataSnapshot) {
        String id = dataSnapshot.child("id").getValue(String.class);
        String category = dataSnapshot.child("category").getValue(String.class);
        DataSnapshot participantSnapshot = dataSnapshot.child("participants");
        List<String> participants = DataSnapshotMapper.listOf(String.class).call(participantSnapshot);
        Class<? extends Event> eventClass = FirebaseEventController.getClassForCategory(category);
        Event event = dataSnapshot.child("event").getValue(eventClass);
        FirebaseEventDate eventDate = dataSnapshot.child("eventDate").getValue(FirebaseEventDate.class);
        FirebaseLocation location = dataSnapshot.child("location").getValue(FirebaseLocation.class);
        DataSnapshot goingSnapshot = dataSnapshot.child("going");
        Map<String, Boolean> going = DataSnapshotMapper.mapOf(Boolean.class).call(goingSnapshot);
        return new FirebaseCompletedPoll(id, category, participants, event, eventDate, location, going);
    }

    /**
     * Returns an observable that will fetch information about the finished poll with
     * the supplied ID.
     * @param pollId ID string of the poll to fetch.
     * @return An {@link Observable} yielding {@link FirebaseCompletedPoll} object
     * with updates about the poll details.
     */
    public static Observable<FirebaseCompletedPoll> getCompletedPoll(String pollId) {
        return RxFirebaseDatabase.observeValueEvent(ref.child(pollId), FirebaseCompletedPollController::map);
    }

    /**
     * Marks whether or not a user will go to a given event.
     * @param pollId Identification string of the poll.
     * @param facebookId Facebok ID of the user.
     * @param going True if the user is going, false otherwise.
     */
    public static void setIsGoing(String pollId, String facebookId, boolean going) {
        ref.child(pollId).child("going").child(facebookId).setValue(going);
    }
}
