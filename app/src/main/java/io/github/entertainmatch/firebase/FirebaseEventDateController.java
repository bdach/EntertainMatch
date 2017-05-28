package io.github.entertainmatch.firebase;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.kelvinapps.rxfirebase.DataSnapshotMapper;
import com.kelvinapps.rxfirebase.RxFirebaseDatabase;
import io.github.entertainmatch.firebase.models.FirebaseEventDate;
import io.github.entertainmatch.firebase.models.FirebasePoll;
import rx.Observable;

import java.util.Map;

/**
 * Firebase controller responsible for handling {@link FirebaseEventDate}
 * objects.
 *
 * @author Adrian Bednarz
 * @since 5/8/17.
 */

public class FirebaseEventDateController {
    /**
     * Instance of the database.
     */
    private static final FirebaseDatabase database = FirebaseDatabase.getInstance();

    /**
     * Holds reference to event dates collection.
     * In this collection each node is denoted by pollId.
     */
    private static final DatabaseReference ref = database.getReference("event_dates");

    /**
     * Method used to initialize date stage.
     * @param poll A {@link FirebasePoll} which is to be moved to
     *             {@link io.github.entertainmatch.model.VoteDateStage}.
     * @param victorious ID string of the event that has won in the
     *                   {@link io.github.entertainmatch.model.VoteEventStage}.
     */
    public static void setupDataStage(FirebasePoll poll, String victorious) {
        getEventDatesSingle(victorious).subscribe(eventDates ->
                setupDateStageForUsers(poll, eventDates));
    }

    /**
     * Method used to initialize date stage.
     * @param poll The poll to set up stage for.
     * @param eventDates Map of available {@link FirebaseEventDate}s.
     */
    static void setupDateStageForUsers(FirebasePoll poll,
                                       Map<String, FirebaseEventDate> eventDates) {
        for (FirebaseEventDate event : eventDates.values()) {
            for (String participantId : poll.getParticipants()) {
                FirebasePollController.setupDateStageForUser(
                        poll.getPollId(),
                        event.getLocationId(),
                        participantId
                );
            }
        }
    }


    /**
     * Retrieves mapping from locationId to event dates from database
     * @param eventId Event id within category
     * @return Returns mapping from locationId to event dates that notifies only once.
     */
    public static Observable<Map<String, FirebaseEventDate>> getEventDatesSingle(String eventId) {
        return RxFirebaseDatabase.observeSingleValueEvent(
                ref.child(eventId),
                DataSnapshotMapper.mapOf(FirebaseEventDate.class)
        );
    }

    /**
     * One time observable for fetching specific event date
     * @param eventId Identifier of an event
     * @param locationId Location identifier of event's venue
     * @return One-time observable with {@code FirebaseEventDate} asked for
     */
    public static Observable<FirebaseEventDate> getEventSingle(String eventId, String locationId) {
        return RxFirebaseDatabase.observeSingleValueEvent(
                ref.child(eventId).child(locationId),
                FirebaseEventDate.class
        );
    }
}
