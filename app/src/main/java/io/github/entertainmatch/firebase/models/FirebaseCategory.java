package io.github.entertainmatch.firebase.models;

import java.util.HashMap;
import java.util.Map;

import lombok.Getter;
import lombok.NoArgsConstructor;

/**
 * Created by Adrian Bednarz on 5/5/17.
 *
 * Holds things that change during Voting stage.
 */

public class FirebaseCategory {
    /**
     * Maps categoryId to number of votes
     */
    @Getter
    private Map<String, Integer> voteCounts = new HashMap<>();

    /**
     * Maps facebookId to categoryId that given user voted for.
     */
    @Getter
    private Map<String, String> votedFor = new HashMap<>();

    public FirebaseCategory() {
        voteCounts.put("A", 1);
        votedFor.put("A", "0");
    }
}