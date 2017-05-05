package io.github.entertainmatch.firebase.models;

import java.util.Map;

import lombok.Getter;
import lombok.NoArgsConstructor;

/**
 * Created by Adrian Bednarz on 5/5/17.
 *
 * Holds things that change during Voting stage.
 */

@NoArgsConstructor
public class FirebaseCategory {
    /**
     * Maps categoryId to number of votes
     */
    @Getter
    private Map<Integer, Integer> voteCounts;

    /**
     * Maps facebookId to categoryId that given user voted for.
     */
    @Getter
    private Map<String, Integer> votedFor;
}
