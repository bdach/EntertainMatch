package io.github.entertainmatch.firebase.models;

import java.util.HashMap;
import java.util.Map;

import lombok.Getter;
import lombok.NoArgsConstructor;

/**
 * Represents user information in Firebase.
 * Used for notification purposes.
 *
 * @author Adrian Bednarz
 * @since 4/30/17
 */
@NoArgsConstructor
@Getter
public class FirebaseUser {
    /**
     * Map used to fire notifications about new polls.
     */
    private Map<String, Boolean> polls = new HashMap<>();

    /**
     * Map used to fire notifications about stage change to
     * {@link io.github.entertainmatch.model.VoteEventStage}.
     */
    private Map<String, Boolean> events = new HashMap<>();

    /**
     * Map used to fire notifications about stage change to
     * {@link io.github.entertainmatch.model.VoteDateStage}.
     */
    private Map<String, Boolean> dates = new HashMap<>();

    /**
     * Map used to fire notifications about stage change to
     * {@link io.github.entertainmatch.model.VoteResultStage}.
     */
    private Map<String, Boolean> finished = new HashMap<>();
}
