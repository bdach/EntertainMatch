package io.github.entertainmatch.firebase.models;

import io.github.entertainmatch.model.Event;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Represents a single completed user poll in Firebase.
 *
 * @author Bartlomiej Dach
 * @since 24.05.17
 */
@AllArgsConstructor
@Getter
public class FirebaseCompletedPoll {
    /**
     * ID string of the poll.
     */
    private String id;

    /**
     * Category chosen by the poll participants.
     */
    private String category;

    /**
     * List of people who participated in the vote.
     */
    private List<String> participants;

    /**
     * The {@link Event} chosen by the users.
     */
    private Event event;

    /**
     * The {@link FirebaseEventDate} chosen by the users.
     */
    private FirebaseEventDate eventDate;

    /**
     * The {@link FirebaseLocation} chosen by the users.
     */
    private FirebaseLocation location;

    /**
     * The keys of this map are Facebook user IDs, and values indicate
     * whether the person with the given ID is going to the event or not.
     */
    private Map<String, Boolean> going;

    /**
     * Updates field values of this {@link FirebaseCompletedPoll} instance.
     * @param updatedPoll {@link FirebaseCompletedPoll} instance containing values to copy.
     */
    public void update(FirebaseCompletedPoll updatedPoll) {
        this.id = updatedPoll.id;
        this.category = updatedPoll.category;
        this.participants = updatedPoll.participants;
        this.event = updatedPoll.event;
        this.eventDate = updatedPoll.eventDate;
        this.location = updatedPoll.location;
        this.going = updatedPoll.going;
    }

    /**
     * Checks whether or not the voting is complete for the given user.
     * @param userId Facebook ID of the user.
     * @return True if the given user has finished voting, false otherwise.
     */
    public boolean votingComplete(String userId) {
        return going != null && going.containsKey(userId);
    }

    /**
     * Gets a list of all poll participants who indicated they would attend
     * the event.
     * @return A {@link List} of Facebook IDs of people who are going.
     */
    public List<String> goingList() {
        ArrayList<String> list = new ArrayList<>();
        for (Map.Entry<String, Boolean> entry : going.entrySet()) {
            if (entry.getValue()) {
                list.add(entry.getKey());
            }
        }
        return list;
    }
}
