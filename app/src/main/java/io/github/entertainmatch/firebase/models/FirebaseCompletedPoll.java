package io.github.entertainmatch.firebase.models;

import io.github.entertainmatch.model.Event;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * @author Bartlomiej Dach
 * @since 24.05.17
 */
@AllArgsConstructor
@Getter
public class FirebaseCompletedPoll {
    private String id;
    private String category;
    private List<String> participants;
    private Event event;
    private FirebaseEventDate eventDate;
    private FirebaseLocation location;
    private Map<String, Boolean> going;

    public void update(FirebaseCompletedPoll updatedPoll) {
        this.id = updatedPoll.id;
        this.category = updatedPoll.category;
        this.participants = updatedPoll.participants;
        this.event = updatedPoll.event;
        this.eventDate = updatedPoll.eventDate;
        this.location = updatedPoll.location;
        this.going = updatedPoll.going;
    }

    public boolean votingComplete(String userId) {
        return going != null && going.containsKey(userId);
    }

    public List<String> goingList() {
        ArrayList<String> list = new ArrayList<>();
        // add only the people who said they were going
        for (Map.Entry<String, Boolean> entry : going.entrySet()) {
            if (entry.getValue()) {
                list.add(entry.getKey());
            }
        }
        return list;
    }
}
