package io.github.entertainmatch.firebase;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import io.github.entertainmatch.firebase.models.FirebaseEventDate;
import io.github.entertainmatch.firebase.models.FirebaseLocation;
import io.github.entertainmatch.firebase.models.FirebasePoll;
import io.github.entertainmatch.model.Event;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import rx.Observable;

import java.util.HashMap;
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
                    node.put("event", event);
                    node.put("eventDate", eventDate);
                    node.put("location", location);
                    return node;
                }
        ).subscribe(node -> ref.child(poll.getPollId()).setValue(node));
    }
}
