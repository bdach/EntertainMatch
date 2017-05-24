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
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * @author Bartlomiej Dach
 * @since 23.05.17
 */
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class FirebaseCompletedPollController {
    private static final FirebaseDatabase database = FirebaseDatabase.getInstance();
    private static final DatabaseReference ref = database.getReference("completed");

    public static void pollCompleted(FirebasePoll poll) {
        Observable<? extends Event> eventSingle = FirebaseEventController.getEventSingle(
                poll.getChosenCategory(),
                poll.getVictoriousEvent().substring(poll.getChosenCategory().length())
        );
        Observable<FirebaseEventDate> eventDateSingle = FirebaseEventDateController.getEventSingle(
                poll.getChosenCategory(),
                poll.getVictoriousEvent(),
                poll.getChosenLocationId()
        );
        Observable<FirebaseLocation> locationSingle = FirebaseLocationsController.getLocationOnce(poll.getChosenLocationId());
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

    public static Observable<FirebaseCompletedPoll> getCompletedPollOnce(String pollId) {
        return RxFirebaseDatabase.observeSingleValueEvent(ref.child(pollId), FirebaseCompletedPollController::map);
    }

    public static List<Observable<FirebaseCompletedPoll>> getPollsForUser(FirebaseUser user) {
        if (user == null) {
            return Collections.emptyList();
        }
        ArrayList<String> pollIds = new ArrayList<>(user.getPolls().keySet());
        return ListExt.map(pollIds, pollId ->
                RxFirebaseDatabase.observeValueEvent(ref.child(pollId), FirebaseCompletedPollController::map));
    }

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

    public static Observable<FirebaseCompletedPoll> getCompletedPoll(String pollId) {
        return RxFirebaseDatabase.observeValueEvent(ref.child(pollId), FirebaseCompletedPollController::map);
    }

    public static void setIsGoing(String pollId, String facebookId, boolean going) {
        ref.child(pollId).child("going").child(facebookId).setValue(going);
    }
}
