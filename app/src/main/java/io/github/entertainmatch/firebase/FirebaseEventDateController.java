package io.github.entertainmatch.firebase;

import android.util.Log;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.kelvinapps.rxfirebase.DataSnapshotMapper;
import com.kelvinapps.rxfirebase.RxFirebaseDatabase;

import java.util.Date;
import java.util.List;
import java.util.Map;

import io.github.entertainmatch.firebase.models.FirebaseEventDate;
import io.github.entertainmatch.firebase.models.FirebasePoll;
import io.github.entertainmatch.model.EventDate;
import rx.Observable;

/**
 * Created by Adrian Bednarz on 5/8/17.
 *
 */

public class FirebaseEventDateController {
    /**
     * Instance of the database
     */
    private static final FirebaseDatabase database = FirebaseDatabase.getInstance();
    /**
     * Holds reference to event dates collection.
     * In this collection each node is denoted by pollId.
     */
    private static final DatabaseReference ref = database.getReference("event_dates");

    /**
     * Method used to initialize date stage
     * @param poll Changed poll
     * @param victorious An event that has won event voting stage (it is not yet present in poll or might not me)
     */
    public static void setup(FirebasePoll poll, String victorious) {
        getEventDatesSingle(poll.getChosenCategory(), victorious).subscribe(events -> {
            for (FirebaseEventDate event : events.values()) {
                for (String participantId : poll.getParticipants()) {
                    FirebasePollController.setupDateStageForUser(poll.getPollId(), event.getLocationId(), participantId);
                }
            }
        });
    }

    /**
     * Retrieves mapping from locationId to event dates from database
     * @param category Category of an event
     * @param eventId Event id within category
     * @return Returns mapping from locationId to event dates that notifies only once.
     */
    public static Observable<Map<String, FirebaseEventDate>> getEventDatesSingle(String category, String eventId) {
        return RxFirebaseDatabase.observeSingleValueEvent(ref.child(category).child(eventId), DataSnapshotMapper.mapOf(FirebaseEventDate.class));
    }

    /**
     * Retrieves mapping from locationId to event dates from database
     * @param category Category of an event
     * @param eventId Event id within category
     * @return Returns mapping from locationId to event dates.
     */
    public static Observable<Map<String, FirebaseEventDate>> getEventDates(String category, String eventId) {
        return RxFirebaseDatabase.observeValueEvent(ref.child(category).child(eventId), DataSnapshotMapper.mapOf(FirebaseEventDate.class));
    }
}
