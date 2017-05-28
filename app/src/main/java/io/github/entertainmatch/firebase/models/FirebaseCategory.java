package io.github.entertainmatch.firebase.models;

import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.HashMap;
import java.util.Map;

/**
 * Holds data about categories retrieved from Firebase.
 *
 * @author Adrian Bednarz
 * @since 5/5/17
 */
@NoArgsConstructor
public class FirebaseCategory {
    /**
     * Maps categoryId to number of votes.
     */
    @Getter
    private Map<String, Integer> voteCounts = new HashMap<>();

    /**
     * Maps facebookId to categoryId that given user voted for.
     */
    @Getter
    private Map<String, String> votedFor = new HashMap<>();
}